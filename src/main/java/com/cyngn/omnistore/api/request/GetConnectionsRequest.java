package com.cyngn.omnistore.api.request;

/**
 * Request object for getting connections.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class GetConnectionsRequest {
    private String sourceId;
    private String sourceType;
    private String connectionType;
    private String scope;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
