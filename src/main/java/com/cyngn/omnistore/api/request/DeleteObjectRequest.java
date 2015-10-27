package com.cyngn.omnistore.api.request;

/**
 * Request object for deleting object.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/18/15.
 */
public class DeleteObjectRequest {
    private String scope;
    private String type;
    private String id;

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
}
