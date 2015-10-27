package com.cyngn.omnistore.util;

import com.cyngn.vertx.web.JsonUtil;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for serializing entity/connection property
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class PropertySerializationUtil {

    public static String serializeMap(Map<String, String> properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }

        /*
            serializing to json string
            TODO: find compact representation.
         */

        // TODO: log if there is corruption
        return JsonUtil.getJsonForObject(properties);
    }

    public static String serializeJson(JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        return jsonObject.encode();
    }


    public static Map<String, String> deserializeToMap(String serialized) {
        if (StringUtils.isBlank(serialized)) {
            return new HashMap<>(0);
        }

        return JsonUtil.parseJsonToObject(serialized, Map.class);
    }

    public static String deserializeToJson(String serialized) {
        if (StringUtils.isBlank(serialized)) {
            return "";
        }

        return new JsonObject(serialized).encode();
    }

}

