package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.GetConnectionRequest;
import com.cyngn.omnistore.api.response.GetConnectionResultHandler;
import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.api.validate.ValidationResult;
import com.cyngn.omnistore.storage.ConnectionStorage;
import com.cyngn.vertx.web.HttpHelper;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Endpoint for retrieving connection from the graph.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class GetConnectionEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(GetConnectionEndpoint.class);

    private final RequestValidator validator;
    private final ConnectionStorage connectionStorage;

    /**
     * Creates endpoint for getting connection.
     *
     * @param validator         - request validator
     * @param connectionStorage - storage operations for connection
     */
    public GetConnectionEndpoint(RequestValidator validator, ConnectionStorage connectionStorage) {
        this.validator = validator;
        this.connectionStorage = connectionStorage;
    }

    /**
     * Handles the get connection request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(GetConnectionRequest request, HttpServerResponse response) {
        ValidationResult result = validator.validateRequest(request);

        if (!result.equals(ValidationResult.SUCCESS)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        connectionStorage.getConnection(request.getSourceType(), request.getSourceId(),
                ScopeUtils.getScope(request.getScope()), request.getConnectionType(),
                request.getDestinationType(), request.getDestinationId(),
                new GetConnectionResultHandler(logger, response));
    }
}
