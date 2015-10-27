package com.cyngn.omnistore.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;

/**
 * Server configuration
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ServerConfig {

    @JsonProperty
    public int port;

    @JsonProperty("cassandra")
    @JsonIgnore
    public JsonObject cassandra;

    @JsonProperty("registry")
    @JsonIgnore
    public JsonObject registry;
}
