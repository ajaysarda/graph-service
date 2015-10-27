package com.cyngn.omnistore.api;

import com.cyngn.omnistore.api.handler.ConnectionHandler;
import com.cyngn.omnistore.api.handler.ObjectHandler;
import com.cyngn.omnistore.registry.TypeRegistry;
import com.cyngn.vertx.web.RestApi;
import com.englishtown.vertx.cassandra.CassandraSession;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.cyngn.omnistore.api.Constants.APP;
import static com.cyngn.omnistore.api.Constants.CONNECTION_TYPE;
import static com.cyngn.omnistore.api.Constants.DST_ID;
import static com.cyngn.omnistore.api.Constants.DST_TYPE;
import static com.cyngn.omnistore.api.Constants.SCOPE;
import static com.cyngn.omnistore.api.Constants.SRC_ID;
import static com.cyngn.omnistore.api.Constants.SRC_TYPE;

/**
 * Entry point for all graph requests.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */

public class GraphApi implements RestApi {

    private static final Logger logger = LoggerFactory.getLogger(GraphApi.class);

    private final static String TRAILING_SLASH = "/";
    private final static String COLON = ":";
    private final static String TRAILING_SLASH_COLON = TRAILING_SLASH + COLON;


    private final static String APP_SCOPE_PATH = TRAILING_SLASH + APP + TRAILING_SLASH_COLON + SCOPE;
    private final static String OBJECT_PATH = TRAILING_SLASH_COLON  + SRC_TYPE + TRAILING_SLASH_COLON + SRC_ID;

    private final static String CONNECTION_ROOT_PATH = OBJECT_PATH + TRAILING_SLASH_COLON + CONNECTION_TYPE;
    private final static String CONNECTION_LEAF_PATH = CONNECTION_ROOT_PATH + TRAILING_SLASH_COLON +
            DST_TYPE + TRAILING_SLASH_COLON + DST_ID;

    private final static String APP_SCOPE_OBJECT_PATH = APP_SCOPE_PATH + OBJECT_PATH;
    private final static String APP_SCOPE_CONNECTION_ROOT_PATH = APP_SCOPE_PATH + CONNECTION_ROOT_PATH;
    private final static String APP_SCOPE_CONNECTION_LEAF_PATH = APP_SCOPE_PATH + CONNECTION_LEAF_PATH;

    // specific request handlers.
    private ConnectionHandler connectionHandler;
    private ObjectHandler objectHandler;

    public GraphApi(CassandraSession session, Vertx vertx, TypeRegistry registry) {
        connectionHandler = new ConnectionHandler(session, vertx, registry);
        objectHandler = new ObjectHandler(session, vertx, registry);
    }

    private RestApiDescriptor[] supportedApi = new RestApiDescriptor[]{
            /*
             * OBJECT APIs
             */

            // Get Object with app scope /app/scope/:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.GET, APP_SCOPE_OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            // Get Object /:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.GET, OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            // Put Object within app scope /app/scope/:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.PUT, APP_SCOPE_OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            // Put object /:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.PUT, OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            // Delete Object within app scope /app/scope/:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.DELETE, APP_SCOPE_OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            // Delete object /:srcType/:srcId/
            new RestApiDescriptor(HttpMethod.DELETE, OBJECT_PATH + TRAILING_SLASH, routingContext -> {
                objectHandler.handle(routingContext);
            }),

            /*
             * CONNECTION APIs
             */

            // Get Connection within app scope /app/scope/:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.GET, APP_SCOPE_CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Get Connection /:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.GET, CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Get Connections within app scope /app/scope/:srcType/:srcId/:connectionType/
            new RestApiDescriptor(HttpMethod.GET, APP_SCOPE_CONNECTION_ROOT_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Get Connections
            new RestApiDescriptor(HttpMethod.GET, CONNECTION_ROOT_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Put Connection within app scope /app/scope/:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.PUT, APP_SCOPE_CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Put Connection /:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.PUT, CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Delete Connection within app scope /app/scope/:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.DELETE, APP_SCOPE_CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),

            // Delete object /:srcType/:srcId/:connectionType/:dstType/:dstId/
            new RestApiDescriptor(HttpMethod.DELETE, CONNECTION_LEAF_PATH + TRAILING_SLASH, routingContext -> {
                connectionHandler.handle(routingContext);
            }),
    };

    @Override
    public RestApiDescriptor[] supportedApi() {
        return supportedApi;
    }
}
