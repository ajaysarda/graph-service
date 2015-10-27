package com.cyngn.omnistore.api.handler;

import com.cyngn.omnistore.api.Constants;
import com.cyngn.omnistore.api.endpoint.DeleteObjectEndpoint;
import com.cyngn.omnistore.api.endpoint.GetObjectEndpoint;
import com.cyngn.omnistore.api.endpoint.PutObjectEndpoint;
import com.cyngn.omnistore.api.request.DeleteObjectRequest;
import com.cyngn.omnistore.api.request.GetObjectRequest;
import com.cyngn.omnistore.api.request.PutObjectRequest;
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
 * Request handler for object based lookups.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class ObjectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ObjectHandler.class);

    // storage objects
    private final ConnectionStorage connectionStorage;
    private final ObjectStorage objectStorage;

    // api endpoints
    private final GetObjectEndpoint getObjectEndpoint;
    private final DeleteObjectEndpoint deleteObjectEndpoint;
    private final PutObjectEndpoint putObjectEndpoint;

    private final RequestValidator validator;

    public ObjectHandler(CassandraSession session, Vertx vertx, TypeRegistry registry) {
        this.validator = new RequestValidator(registry);

        this.connectionStorage = new ConnectionStorage(session, vertx, registry);
        this.objectStorage = new ObjectStorage(session, vertx);

        this.getObjectEndpoint = new GetObjectEndpoint(validator, objectStorage);
        this.deleteObjectEndpoint = new DeleteObjectEndpoint(vertx, validator, objectStorage, connectionStorage);
        this.putObjectEndpoint = new PutObjectEndpoint(validator, objectStorage);
    }

    public void handle(RoutingContext context) {
        HttpServerRequest request = context.request();

        if (request.method() == HttpMethod.GET) {
            GetObjectRequest getObjectRequest = createGetObjectRequest(request.params());
            getObjectEndpoint.handle(getObjectRequest, request.response());
        } else if (request.method() == HttpMethod.PUT) {
            // extract the properties from body and set the properties.
            PutObjectRequest putObjectRequest = createPutObjectRequest(request.params());
            request.bodyHandler(body -> {
                JsonObject jsonObject = new JsonObject(body.toString());
                putObjectRequest.setProperties(PropertySerializationUtil.deserializeToMap(jsonObject.encode()));
                putObjectEndpoint.handle(putObjectRequest, request.response());
            });
        } else if (request.method() == HttpMethod.DELETE) {
            DeleteObjectRequest deleteObjectRequest = createDeleteObjectRequest(request.params());
            deleteObjectEndpoint.handle(deleteObjectRequest, request.response());
        }
    }

    private GetObjectRequest createGetObjectRequest(MultiMap params) {
        GetObjectRequest getObjectRequest = new GetObjectRequest();
        getObjectRequest.setScope(params.get(Constants.SCOPE));
        getObjectRequest.setId(params.get(Constants.SRC_ID));
        getObjectRequest.setType(params.get(Constants.SRC_TYPE));
        return getObjectRequest;
    }

    private PutObjectRequest createPutObjectRequest(MultiMap params) {
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setScope(params.get(Constants.SCOPE));
        putObjectRequest.setId(params.get(Constants.SRC_ID));
        putObjectRequest.setType(params.get(Constants.SRC_TYPE));

        return putObjectRequest;
    }

    private DeleteObjectRequest createDeleteObjectRequest(MultiMap params) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest();
        deleteObjectRequest.setScope(params.get(Constants.SCOPE));
        deleteObjectRequest.setId(params.get(Constants.SRC_ID));
        deleteObjectRequest.setType(params.get(Constants.SRC_TYPE));
        return deleteObjectRequest;
    }

}
