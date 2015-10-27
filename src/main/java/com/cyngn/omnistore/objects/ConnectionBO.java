package com.cyngn.omnistore.objects;

import com.cyngn.omnistore.constants.ConnectionConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

/**
 * Business object for Connection
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ConnectionBO {

    @JsonProperty(ConnectionConstants.SRC_ID)
    public String srcId;

    @JsonProperty(ConnectionConstants.SRC_TYPE)
    public String srcType;

    @JsonProperty(ConnectionConstants.TYPE)
    public String type;

    @JsonProperty(ConnectionConstants.DST_ID)
    public String dstId;

    @JsonProperty(ConnectionConstants.DST_TYPE)
    public String dstType;

    @JsonProperty("scope")
    public String scope;

    @JsonProperty(ConnectionConstants.CREATED)
    public Date created;

    @JsonProperty(ConnectionConstants.LAST_UPDATED)
    public Date lastUpdated;

    @JsonProperty(ConnectionConstants.PROPERTIES)
    public Map<String, String> properties;


    public ConnectionBO(String srcId, String srcType, String type, String dstId, String dstType, String scope, Date created, Date lastUpdated, Map<String, String> properties) {
        this.srcId = srcId;
        this.srcType = srcType;
        this.type = type;
        this.dstId = dstId;
        this.dstType = dstType;
        this.scope = scope;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.properties = properties;
    }
}
