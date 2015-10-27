package com.cyngn.omnistore.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

/**
 * Data object for Object.
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ObjectBO {

    @JsonProperty("id")
    public String id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("scope")
    public String scope;

    @JsonProperty("created")
    public Date created;

    @JsonProperty("last_updated")
    public Date lastUpdated;

    @JsonProperty("properties")
    public Map<String, String> properties;

    public ObjectBO(String id, String type, String scope, Date created, Date lastUpdated, Map<String, String> properties) {
        this.id = id;
        this.type = type;
        this.scope = scope;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.properties = properties;
    }
}
