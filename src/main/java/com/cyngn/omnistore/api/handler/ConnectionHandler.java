package com.cyngn.omnistore.api.handler;

import com.cyngn.omnistore.api.Constants;
import com.cyngn.omnistore.api.endpoint.DeleteConnectionEndpoint;
import com.cyngn.omnistore.api.endpoint.GetConnectionEndpoint;
import com.cyngn.omnistore.api.endpoint.GetConnectionsEndpoint;
import com.cyngn.omnistore.api.endpoint.PutConnectionEndpoint;
import com.cyngn.omnistore.api.request.DeleteConnectionRequest;
import com.cyngn.omnistore.api.request.GetConnectionRequest;
import com.cyngn.omnistore.api.request.GetConnectionsRequest;
import com.cyngn.omnistore.api.request.PutConnectionRequest;
import com.cyngn.omnistore.api.validate.RequestValidator;
import com.cyngn.omnistore.registry.TypeRegistry;
import com.cyngn.omnistore.storage.ConnectionStorage;
import com.cyngn.omnistore.storage.ObjectStorage;
import com.cyngn.omnistore.util.PropertySerializationUtil;
import com.englishtown.vertx.cassandra.CassandraSession;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request handler for connection based query
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class ConnectionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    private final GetConnectionEndpoint getConnectionEndpoint;
    private final GetConnectionsEndpoint getConnectionsEndpoint;
    private final PutConnectionEndpoint putConnectionEndpoint;
    private final DeleteConnectionEndpoint deleteConnectionEndpoint;
    private final RequestValidator validator;
    private final ConnectionStorage connectionStorage;
    private final ObjectStorage objectStorage;

    public ConnectionHandler(CassandraSession session, Vertx vertx, TypeRegistry registry) {
        this.validator = new RequestValidator(registry);
        this.connectionStorage = new ConnectionStorage(session, vertx, registry);
        this.objectStorage = new ObjectStorage(session, vertx);
        this.getConnectionEndpoint = new GetConnectionEndpoint(validator, connectionStorage);
        this.getConnectionsEndpoint = new GetConnectionsEndpoint(validator, connectionStorage);
        this.putConnectionEndpoint = new PutConnectionEndpoint(vertx, validator, connectionStorage, objectStorage);
        this.deleteConnectionEndpoint = new DeleteConnectionEndpoint(vertx, validator, connectionStorage, objectStorage);
    }

    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();

        if (request.method() == HttpMethod.GET) {
            if (request.params().contains(Constants.DST_ID) && request.params().contains(Constants.DST_TYPE)) {
                getConnectionEndpoint.handle(createGetConnectionRequest(request.params()), request.response());
            } else {
                getConnectionsEndpoint.handle(createGetConnectionsRequest(request.params()), request.response());
            }
        } else if (request.method() == HttpMethod.PUT) {
            PutConnectionRequest putConnectionRequest = createPutConnectionRequest(request.params());
            // validate both dstType and dstId
            request.bodyHandler(body -> {
                JsonObject jsonObject = null;
                if (body == null || body.toString().equals("\n") || body.toString().isEmpty()) {
                    jsonObject = new JsonObject();
                } else {
                    jsonObject = new JsonObject(body.toString());
                }
                putConnectionRequest.setProperties(PropertySerializationUtil.deserializeToMap(jsonObject.encode()));
                putConnectionEndpoint.handle(putConnectionRequest, request.response());
            });
        } else if (request.method() == HttpMethod.DELETE) {
            deleteConnectionEndpoint.handle(createDeleteConnectionRequest(request.params()), request.response());
        }
    }

    private GetConnectionRequest createGetConnectionRequest(MultiMap params) {
        GetConnectionRequest request = new GetConnectionRequest();
        request.setScope(params.get(Constants.SCOPE));
        request.setSourceId(params.get(Constants.SRC_ID));
        request.setSourceType(params.get(Constants.SRC_TYPE));
        request.setConnectionType(params.get(Constants.CONNECTION_TYPE));
        request.setDestinationId(params.get(Constants.DST_ID));
        request.setDestinationType(params.get(Constants.DST_TYPE));
        return request;
    }

    private GetConnectionsRequest createGetConnectionsRequest(MultiMap params) {
        GetConnectionsRequest request = new GetConnectionsRequest();
        request.setScope(params.get(Constants.SCOPE));
        request.setSourceId(params.get(Constants.SRC_ID));
        request.setSourceType(params.get(Constants.SRC_TYPE));
        request.setConnectionType(params.get(Constants.CONNECTION_TYPE));
        return request;
    }

    private PutConnectionRequest createPutConnectionRequest(MultiMap params) {
        PutConnectionRequest request = new PutConnectionRequest();
        request.setScope(params.get(Constants.SCOPE));
        request.setSourceId(params.get(Constants.SRC_ID));
        request.setSourceType(params.get(Constants.SRC_TYPE));
        request.setConnectionType(params.get(Constants.CONNECTION_TYPE));
        request.setDestinationId(params.get(Constants.DST_ID));
        request.setDestinationType(params.get(Constants.DST_TYPE));
        return request;
    }

    private DeleteConnectionRequest createDeleteConnectionRequest(MultiMap params) {
        DeleteConnectionRequest request = new DeleteConnectionRequest();
        request.setScope(params.get(Constants.SCOPE));
        request.setSourceId(params.get(Constants.SRC_ID));
        request.setSourceType(params.get(Constants.SRC_TYPE));
        request.setConnectionType(params.get(Constants.CONNECTION_TYPE));
        request.setDestinationId(params.get(Constants.DST_ID));
        request.setDestinationType(params.get(Constants.DST_TYPE));
        return request;
    }
}


