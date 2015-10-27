package com.cyngn.omnistore.registry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Metadata around object type
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class ObjectType {

    @JsonProperty("name")
    public String name;

    @JsonProperty("properties")
    public List<String> properties;

    public ObjectType(String name, List<String> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }
}
