package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.PutConnectionRequest;
import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.storage.ConnectionStorage;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.storage.dataobjects.ObjectDO;
import com.cyngn.omnistore.util.PropertySerializationUtil;
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
 * Endpoint for putting connections into graph
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class PutConnectionEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(PutConnectionEndpoint.class);
    public static final String SRC = "src";
    public static final String DST = "dst";

    private final ConnectionStorage connectionStorage;
    private final ObjectStorage objectStorage;
    private final Vertx vertx;
    private final RequestValidator validator;

    /**
     * Creates endpoint for put connection
     *
     * @param vertx             - reference to vertx
     * @param validator         - request validator
     * @param connectionStorage - storage operations for connection
     * @param objectStorage     - storage operations for object.
     */
    public PutConnectionEndpoint(Vertx vertx, RequestValidator validator,
                                 ConnectionStorage connectionStorage,
                                 ObjectStorage objectStorage) {
        this.vertx = vertx;
        this.validator = validator;

        this.connectionStorage = connectionStorage;
        this.objectStorage = objectStorage;
    }

    /**
     * Handles the put connection request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(PutConnectionRequest request, HttpServerResponse response) {
        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        Promise promise = Promise.newInstance(vertx);

        promise.all((context, onResult) -> {
            getSourceObject(request, context, onResult);
        }, (context, onResult) -> {
            getDestinationObject(request, context, onResult);
        }).then((context, onResult) -> {
            if (!context.containsKey(SRC)) {
                HttpHelper.processErrorResponse("object with id: " + request.getSourceId() +
                        " does not exist", response, HttpResponseStatus.BAD_REQUEST.code());

                //break the promise chain
                onResult.accept(false);
                return;
            } else if (!context.containsKey(DST)) {
                HttpHelper.processErrorResponse("object with id: " + request.getDestinationId() + " does not exist", response,
                        HttpResponseStatus.BAD_REQUEST.code());

                //break the promise chain
                onResult.accept(false);
                return;
            }
            putConnection(request, response, onResult);
        }).except(context -> {
            logger.error("Promise execution failed with ex:{}", context.getString(Promise.CONTEXT_FAILURE_KEY));
        }).eval();
    }

    private void putConnection(PutConnectionRequest request, final HttpServerResponse response, final Consumer<Boolean> onResult) {
        connectionStorage.putConnection(request.getSourceId(), request.getSourceType(), ScopeUtils.getScope(request.getScope()),
                request.getConnectionType(), request.getDestinationId(), request.getDestinationType(), PropertySerializationUtil.serializeMap(request.getProperties()),
                new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess(ResultSet result) {
                        HttpHelper.processResponse(response);
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("object put failed with ", t);
                        HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                        onResult.accept(false);
                    }
                });
    }

    private void getDestinationObject(PutConnectionRequest request, final JsonObject context, final Consumer<Boolean> onResult) {
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
                        logger.error("getObject failed with ex: {}", t);
                        onResult.accept(false);
                    }
                });
    }

    private void getSourceObject(PutConnectionRequest request, final JsonObject context, final Consumer<Boolean> onResult) {
        objectStorage.getObject(request.getSourceType(), request.getSourceId(), ScopeUtils.getScope(request.getScope()),
                new FutureCallback<ObjectDO>() {
                    @Override
                    public void onSuccess(ObjectDO result) {
                        if (result != null) {
                            context.put(SRC, true);
                        }
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("getObject failed with ex: {}", t);
                        onResult.accept(false);
                    }
                });
    }

}
