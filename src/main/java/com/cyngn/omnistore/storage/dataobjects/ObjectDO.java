package com.cyngn.omnistore.storage.dataobjects;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;

/**
 * Data object for Object
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
@Table(keyspace = "omnistore", name = "objects")
public class ObjectDO {

    @PartitionKey(value = 0)
    @Column(name = "id")
    public String id;

    @PartitionKey(value = 1)
    @Column(name = "type")
    public String type;

    @Column(name = "scope")
    public String scope;

    @Column(name = "created")
    public Date created;

    @Column(name = "last_updated")
    public Date lastUpdated;

    @Column(name = "properties")
    public String properties;

    public ObjectDO() {
    }

    public ObjectDO(String id, String type, String scope, Date created, Date lastUpdated, String properties) {
        this.id = id;
        this.type = type;
        this.scope = scope;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
