package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.DeleteObjectRequest;
import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.storage.ConnectionStorage;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.storage.dataobjects.ObjectDO;
import com.cyngn.vertx.async.promise.Promise;
import com.cyngn.vertx.web.HttpHelper;
import com.datastax.driver.core.ResultSet;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Endpoint for deleting object from graph storage
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class DeleteObjectEndpoint {
    private final static Logger logger = LoggerFactory.getLogger(DeleteObjectEndpoint.class);

    private final Vertx vertx;
    private final com.cyngn.omnistore.api.validate.RequestValidator validator;
    private final ObjectStorage objectStorage;
    private final ConnectionStorage connectionStorage;

    /**
     * Creates endpoint for deleting object.
     *
     * @param vertx             - reference to vertx
     * @param validator         - request validator
     * @param connectionStorage - storage operations for connection
     * @param objectStorage     - storage operations for object.
     */
    public DeleteObjectEndpoint( Vertx vertx, RequestValidator validator,
                                 ObjectStorage objectStorage,
                                 ConnectionStorage connectionStorage) {
        this.vertx = vertx;
        this.validator = validator;
        this.objectStorage = objectStorage;
        this.connectionStorage = connectionStorage;
    }

    /**
     * Handles the delete object request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(DeleteObjectRequest request, HttpServerResponse response) {
        // validate the request.

        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        Promise promise = Promise.newInstance(vertx);
        String obj = "obj";
        String notExists = "not_exists";

        promise.then((context, onResult) -> {
            getObject(request, response, obj, notExists, context, onResult);
        }).then((context, onResult) -> {
            if (context.containsKey(obj)) {
                onResult.accept(true);
                return;
            }
            deleteObject(request, response, onResult);
        }).then((context1, onResult) -> {
            //successfully deleted the object

            // clean up all connections where obj is source and destination
            // action to clean up connections should be published and
            // other listener should keep on consuming and cleaning up connections.

            // But doing there in request path asynchronously.
            // Alternatively we can soft delete the object (PRIVACY - concerns)

            deleteConnections(request);

        }).eval();
    }

    private void deleteConnections(final DeleteObjectRequest request) {
        connectionStorage.deleteConnection(request.getType(), request.getId(), ScopeUtils.getScope(request.getScope()), new FutureCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet result) {
                logger.info("Successfully deleted connections for type: {} and id: {}", request.getType(), request.getId());
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("Failed to delete connections for type:{}, id:{} with ex:{}", request.getType(), request.getId(), t);
            }
        });
    }

    private void deleteObject(DeleteObjectRequest request, final HttpServerResponse response, final Consumer<Boolean> onResult) {
        objectStorage.deleteObject(request.getType(), request.getId(), ScopeUtils.getScope(request.getScope()), new FutureCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet result) {
                HttpHelper.processResponse(response);
                onResult.accept(true);
            }

            @Override
            public void onFailure(Throwable t) {
                logger.error("object delete failed with ", t);
                HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                onResult.accept(false);
            }
        });
    }

    private void getObject(final DeleteObjectRequest request, final HttpServerResponse response, final String obj, final String notExists, final JsonObject context, final Consumer<Boolean> onResult) {
        objectStorage.getObject(request.getType(), request.getId(), ScopeUtils.getScope(request.getScope()), new FutureCallback<ObjectDO>() {
            @Override
            public void onSuccess(ObjectDO result) {
                if (result != null) {
                    onResult.accept(true);
                } else {
                    HttpHelper.processResponse("Object with id: " + request.getId() + " does not exist", response,
                            HttpResponseStatus.BAD_REQUEST.code());
                    context.put(obj, notExists);
                    onResult.accept(true);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                context.put(obj, notExists);
                onResult.accept(true);
            }
        });
    }

}
