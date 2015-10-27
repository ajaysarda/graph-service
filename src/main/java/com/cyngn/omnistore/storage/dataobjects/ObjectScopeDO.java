package com.cyngn.omnistore.storage.dataobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

/**
 * Data object for Object scope
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ObjectScopeDO {

    @JsonProperty("id")
    public String id;

    @JsonProperty("type")
    public String type;

    @JsonProperty("scope")
    public String scope;

    @JsonProperty("created")
    public Date created;

    @JsonProperty("lastUpdated")
    public Date lastUpdated;

    @JsonProperty("properties")
    public Map<String, String> properties;


}
