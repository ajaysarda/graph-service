package com.cyngn.omnistore.api.request;

import java.util.Map;

/**
 * Request object for putting object.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class PutObjectRequest {
    private String scope;
    private String type;
    private String id;
    private Map<String, String> properties;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
