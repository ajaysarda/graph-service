package com.cyngn.omnistore.api.endpoint;

import com.cyngn.omnistore.api.request.GetConnectionsRequest;
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
 * Endpoint for getting connections from the graph.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class GetConnectionsEndpoint {

    private final static Logger logger = LoggerFactory.getLogger(GetConnectionsEndpoint.class);

    private final ConnectionStorage connectionStorage;
    private final RequestValidator validator;

    /**
     * Creates endpoint for getting connections.
     *
     * @param validator         - request validator
     * @param connectionStorage - storage operations for connection
     */
    public GetConnectionsEndpoint(RequestValidator validator, ConnectionStorage connectionStorage) {
        this.validator = validator;
        this.connectionStorage = connectionStorage;
    }

    /**
     * Handles the get connection request
     *
     * @param request  - request object for delete connection
     * @param response - http response object
     */
    public void handle(GetConnectionsRequest request, HttpServerResponse response) {
        ValidationResult result = validator.validateRequest(request);

        if (!ValidationResult.SUCCESS.equals(result)) {
            HttpHelper.processErrorResponse(result.message, response, HttpResponseStatus.BAD_REQUEST.code());
            return;
        }

        connectionStorage.getConnections(request.getSourceType(), request.getSourceId(), ScopeUtils.getScope(request.getScope()),
                request.getConnectionType(), new GetConnectionResultHandler(logger, response));
    }

}
