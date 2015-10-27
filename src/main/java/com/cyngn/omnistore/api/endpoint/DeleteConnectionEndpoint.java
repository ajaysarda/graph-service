package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.DeleteConnectionRequest;
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
 * Endpoint for deleting connection from the graph.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class DeleteConnectionEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(DeleteConnectionEndpoint.class);

    private final ConnectionStorage connectionStorage;
    private final ObjectStorage objectStorage;
    private final Vertx vertx;
    private final RequestValidator validator;
    private final String SRC = "src";
    private final String DST = "dst";

    /**
     * Creates endpoint for delete connection
     *
     * @param vertx             - reference to vertx
     * @param validator         - request validator
     * @param connectionStorage - storage operations for connection
     * @param objectStorage     - storage operations for object.
     */
    public DeleteConnectionEndpoint(Vertx vertx, RequestValidator validator,
                                    ConnectionStorage connectionStorage,
                                    ObjectStorage objectStorage) {
        this.vertx = vertx;
        this.validator = validator;
        this.connectionStorage = connectionStorage;
        this.objectStorage = objectStorage;
    }

    /**
     * Handles the delete connection request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(DeleteConnectionRequest request, HttpServerResponse response) {
        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }
        Promise promise = Promise.newInstance(vertx);

        promise.all((context, onResult) -> getSourceObject(request, context, onResult),
            (context, onResult) -> getDestinationObject(request, context, onResult)
        ).then((context, onResult) -> {
            if (!context.containsKey(SRC)) {
                HttpHelper.processErrorResponse("object with id: " + request.getSourceId() + " does not exist",
                        response, HttpResponseStatus.BAD_REQUEST.code());

                //break the promise chain
                onResult.accept(false);
                return;
            } else if (!context.containsKey(DST)) {
                HttpHelper.processErrorResponse("object with id: " + request.getDestinationId() + " does not exist",
                        response, HttpResponseStatus.BAD_REQUEST.code());

                //break the promise chain
                onResult.accept(false);
                return;
            }
            deleteConnection(request, context, onResult, response);
        }).except(context -> {
            logger.error("Promise execution failed with ex:", context.getString(Promise.CONTEXT_FAILURE_KEY));
        }).eval();
    }

    private void getSourceObject(DeleteConnectionRequest request, JsonObject context, Consumer<Boolean> onResult) {
        objectStorage.getObject(request.getSourceType(), request.getSourceId(),
                ScopeUtils.getScope(request.getScope()), new FutureCallback<ObjectDO>() {
                    @Override
                    public void onSuccess(ObjectDO result) {
                        if (result != null) {
                            context.put(SRC, true);
                        }
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("getObject failed with ex:", t);
                        onResult.accept(false);
                    }
                });
    }

    private void getDestinationObject(DeleteConnectionRequest request, JsonObject context, Consumer<Boolean> onResult) {
        objectStorage.getObject(request.getDestinationType(), request.getDestinationId(), ScopeUtils.getScope(request.getScope()),
                new FutureCallback<ObjectDO>() {
                    @Override
                    public void onSuccess(ObjectDO result) {
                        if (result != null) {
                            context.put(DST, true);
                        }
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("getObject failed with ex: ", t);
                        onResult.accept(false);
                    }
                });
    }

    private void deleteConnection(DeleteConnectionRequest request, JsonObject context, Consumer<Boolean> onResult, HttpServerResponse response) {
        connectionStorage.deleteConnection(request.getSourceType(), request.getSourceId(), ScopeUtils.getScope(request.getScope()),
                request.getConnectionType(), request.getDestinationType(), request.getDestinationId(), new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess(ResultSet result) {
                        HttpHelper.processResponse(response);
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("delete connection failed with ex: ", t);
                        HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        onResult.accept(false);
                    }
                });
    }

}
