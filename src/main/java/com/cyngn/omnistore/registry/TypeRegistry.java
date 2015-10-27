package com.cyngn.omnistore.registry;

import com.google.common.collect.Maps;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Registry of all object and connection types known to this service.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class TypeRegistry {
    private Map<String, ObjectType> objectTypeRegistry = Maps.newHashMap();
    private Map<String, ConnectionType> connectionTypeRegistry = Maps.newHashMap();

    public TypeRegistry(JsonObject config) {
        bootStrapRegistry(config);
    }

    private void bootStrapRegistry(JsonObject config) {
        JsonArray objectTypes = config.getJsonArray("objects");

        for (int i =0; i < objectTypes.size(); i++) {
            JsonObject object = (JsonObject) objectTypes.getValue(i);

            ObjectType type =
                    new ObjectType(object.getString("name"), object.getJsonArray("properties").getList());

            objectTypeRegistry.put(type.name, type);
        }

        JsonArray connectionTypes = config.getJsonArray("connections");

        for (int i =0; i < connectionTypes.size(); i++) {
            JsonObject object = (JsonObject) connectionTypes.getValue(i);

            ConnectionType type =
                    new ConnectionType(object.getString("name"), object.getBoolean("directed"),
                            object.getBoolean("multi"), object.getJsonArray("pairs").getList());

            connectionTypeRegistry.put(type.name, type);
        }
    }

    public ObjectType getObjectType(String name) {
        return objectTypeRegistry.get(name);
    }

    public ConnectionType getConnectionType(String name) {
        return connectionTypeRegistry.get(name);
    }

    public boolean isValidObjectType (String name) {
        return objectTypeRegistry.containsKey(name);
    }

    public boolean isValidConnectionType (String name) {
        return connectionTypeRegistry.containsKey(name);
    }
}
