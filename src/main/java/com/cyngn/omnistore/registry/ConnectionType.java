package com.cyngn.omnistore.registry;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Metadata around Connection
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class ConnectionType {

    @JsonProperty("name")
    public String name;

    @JsonProperty("directed")
    public boolean directed;

    @JsonProperty("multi")
    public boolean multi;

    @JsonProperty("pairs")
    public List<String> pairs;

    public ConnectionType(String name, boolean directed, boolean multi, List<String> pairs) {
        this.name = name;
        this.directed = directed;
        this.multi = multi;
        this.pairs = pairs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isMulti() {
        return multi;
    }

    public void setMulti(boolean multi) {
        this.multi = multi;
    }

    public List<String> getPairs() {
        return pairs;
    }

    public void setPairs(List<String> pairs) {
        this.pairs = pairs;
    }

    public boolean isValidConnection(String srcType, String dstType) {
        boolean found = false;
        for (String pair: pairs) {
            String[] parts = pair.split(":");
            if (parts[0].equals(srcType) && parts[1].equals(dstType)) {
                found = true;
            } else if (!directed && parts[1].equals(srcType) && parts[0].equals(dstType)) {
                found = true;
            }

            if(found)
                return found;
        }

        return false;
    }

    public boolean isValidConnectionFrom(String srcType) {
        boolean found = false;
        for (String pair: pairs) {
            String[] parts = pair.split(":");
            if (parts[0].equals(srcType)) {
                found = true;
            } else if (!directed && parts[1].equals(srcType)) {
                found = true;
            }

            if(found)
                return found;
        }

        return false;
    }

    public boolean isValidConnectionTo(String dstType) {
        boolean found = false;
        for (String pair: pairs) {
            String[] parts = pair.split(":");
            if (parts[1].equals(dstType)) {
                found = true;
            } else if (!directed && parts[0].equals(dstType)) {
                found = true;
            }

            if(found)
                return found;
        }

        return false;
    }
}
