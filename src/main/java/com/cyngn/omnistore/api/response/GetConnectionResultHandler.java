package com.cyngn.omnistore.api.response;

import com.cyngn.omnistore.constants.ConnectionConstants;
import com.cyngn.omnistore.storage.dataobjects.ConnectionDO;
import com.cyngn.omnistore.util.PropertySerializationUtil;
import com.cyngn.vertx.web.HttpHelper;
import com.cyngn.vertx.web.JsonUtil;
import com.datastax.driver.mapping.Result;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;

import java.util.List;

/**
 * Handler for connections result.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/28/15.
 */
public class GetConnectionResultHandler implements FutureCallback<Result<ConnectionDO>> {
    private final Logger logger;
    private final HttpServerResponse response;

    public GetConnectionResultHandler(Logger logger, HttpServerResponse response) {
        this.logger = logger;
        this.response = response;

    }
    @Override
    public void onSuccess(Result<ConnectionDO> result) {
        // construct json array object
        List<JsonObject> connections = Lists.newArrayList();
        for (ConnectionDO connectionDO : result) {

            // srcId, srcType, type is redundant information. so not sending back.
            JsonObject object = new JsonObject();
            object.put(ConnectionConstants.DST_ID, connectionDO.dstId);
            object.put(ConnectionConstants.DST_TYPE, connectionDO.dstType);
            object.put(ConnectionConstants.CREATED, JsonUtil.getJsonForObject(connectionDO.created));
            object.put(ConnectionConstants.LAST_UPDATED, JsonUtil.getJsonForObject(connectionDO.lastUpdated));
            object.put(ConnectionConstants.PROPERTIES, PropertySerializationUtil.deserializeToMap(connectionDO.properties));

            connections.add(object);
        }

        HttpHelper.processResponse(connections, response);
    }

    @Override
    public void onFailure(Throwable t) {
        logger.error("connection get failed with ", t);
        HttpHelper.processResponse(response, HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    }
}
