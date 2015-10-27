package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.PutObjectRequest;
import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.util.PropertySerializationUtil;
import com.cyngn.vertx.web.HttpHelper;
import com.datastax.driver.core.ResultSet;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint for putting objects in graph
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class PutObjectEndpoint {
    private final static Logger logger = LoggerFactory.getLogger(PutObjectEndpoint.class);

    private final ObjectStorage objectStorage;
    private final RequestValidator validator;

    /**
     * Creates endpoint for putting object.
     *
     * @param validator     - request validator
     * @param objectStorage - storage operations for object
     */
    public PutObjectEndpoint(RequestValidator validator, ObjectStorage objectStorage) {
        this.validator = validator;
        this.objectStorage = objectStorage;
    }

    /**
     * Handles the put object request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(PutObjectRequest request, HttpServerResponse response) {
        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        objectStorage.putObject(request.getType(), request.getId(), ScopeUtils.getScope(request.getScope()),
                PropertySerializationUtil.serializeMap(request.getProperties()),
                new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess(ResultSet result) {
                        HttpHelper.processResponse(response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.error("object put failed with ", t);
                        HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
                    }
                });
    }

}
