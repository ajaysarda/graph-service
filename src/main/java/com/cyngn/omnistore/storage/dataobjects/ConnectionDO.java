package com.cyngn.omnistore.storage.dataobjects;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;

/**
 * Data Object for connections
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
@Table(keyspace = "omnistore", name = "connections")
public class ConnectionDO {

    @PartitionKey(value = 0)
    @Column(name = "src_id")
    public String srcId;

    @PartitionKey(value = 1)
    @Column(name ="src_type")
    public String srcType;

    @PartitionKey(value = 2)
    @Column(name ="type")
    public String type;

    @Column(name = "dst_id")
    public String dstId;

    @Column(name = "dst_type")
    public String dstType;

    @Column(name = "scope")
    public String scope;

    @Column(name = "created")
    public Date created;

    @Column(name = "last_updated")
    public Date lastUpdated;

    @Column(name = "properties")
    public String properties;

    public ConnectionDO() {

    }

    public ConnectionDO(String srcId, String srcType, String scope, String type, String dstId, String dstType, Date created, Date lastUpdated, String properties) {
        this.srcId = srcId;
        this.srcType = srcType;
        this.scope = scope;
        this.type = type;
        this.dstId = dstId;
        this.dstType = dstType;
        this.created = created;
        this.lastUpdated = lastUpdated;
        this.properties = properties;
    }

    public String getSrcId() {
        return srcId;
    }

    public void setSrcId(String srcId) {
        this.srcId = srcId;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDstId() {
        return dstId;
    }

    public void setDstId(String dstId) {
        this.dstId = dstId;
    }

    public String getDstType() {
        return dstType;
    }

    public void setDstType(String dstType) {
        this.dstType = dstType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
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
}
