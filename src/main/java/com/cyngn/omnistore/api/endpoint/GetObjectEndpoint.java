package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.GetObjectRequest;
import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.objects.ObjectBO;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.storage.dataobjects.ObjectDO;
import com.cyngn.omnistore.util.PropertySerializationUtil;
import com.cyngn.vertx.web.HttpHelper;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint for retrieving object from graph storage
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class GetObjectEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(GetObjectEndpoint.class);

    private final ObjectStorage objectStorage;
    private final RequestValidator validator;

    /**
     * Creates endpoint for getting object.
     *
     * @param validator     - request validator
     * @param objectStorage - storage operations for object
     */
    public GetObjectEndpoint(RequestValidator validator, ObjectStorage objectStorage) {
        this.validator = validator;
        this.objectStorage = objectStorage;
    }

    /**
     * Handles the get object request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(GetObjectRequest request, HttpServerResponse response) {
        // validate the request.

        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        objectStorage.getObject(request.getType(), request.getId(), ScopeUtils.getScope(request.getScope()),
                new GetObjectResultHandler(logger, response));
    }

    class GetObjectResultHandler implements FutureCallback<ObjectDO> {
        private final Logger logger;
        private final HttpServerResponse response;

        public GetObjectResultHandler(Logger logger, HttpServerResponse response) {
            this.logger = logger;
            this.response = response;

        }

        @Override
        public void onSuccess(ObjectDO result) {
            if (result != null) {
                ObjectBO objectBO = new ObjectBO(result.id, result.type, result.scope, result.created, result.lastUpdated,
                        PropertySerializationUtil.deserializeToMap(result.properties));

                HttpHelper.processResponse(objectBO, response);
            } else {
                HttpHelper.processResponse(response, HttpResponseStatus.NO_CONTENT.code());
            }
        }

        @Override
        public void onFailure(Throwable t) {
            logger.error("object get failed with ", t);
            HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        }
    }
}
