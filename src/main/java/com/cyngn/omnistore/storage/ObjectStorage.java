package com.cyngn.omnistore.storage;

import com.cyngn.omnistore.api.utils.ScopeUtils;
import com.cyngn.omnistore.storage.dataobjects.ObjectDO;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.englishtown.vertx.cassandra.CassandraSession;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.vertx.core.Vertx;

/**
 * Cassandra Storage implementation for Objects
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ObjectStorage {
    private final ObjectAccessor objectAccessor;

    public ObjectStorage(CassandraSession session, Vertx vertx) {
        MappingManager manager = new MappingManager(session.getSession());
        objectAccessor = manager.createAccessor(ObjectAccessor.class);
    }

    /**
     * Insert object in the graph storage
     *
     * @param type       - object type
     * @param id         - object Id
     * @param properties - object properties
     * @param callback   - callback on success
     */
    public void putObject(String type, String id, String properties, FutureCallback<ResultSet> callback) {
        putObject(type, id, ScopeUtils.DEFAULT_SCOPE, properties, callback);
    }

    public void putObject(String type, String id, String scope, String properties, FutureCallback<ResultSet> callback) {
        ListenableFuture<ResultSet> future = objectAccessor.putObject(id, type, scope, properties);

        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void deleteObject(String type, String id, FutureCallback<ResultSet> callback) {
        deleteObject(type, id, ScopeUtils.DEFAULT_SCOPE, callback);
    }

    public void deleteObject(String type, String id, String scope, FutureCallback<ResultSet> callback) {
        ListenableFuture<ResultSet> future = objectAccessor.deleteObject(id, type, scope);

        Futures.addCallback(future, new FutureCallback<ResultSet>() {
            @Override
            public void onSuccess(ResultSet result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });

    }

    public void getObject(String type, String id, FutureCallback<ObjectDO> callback) {
        getObject(type, id, ScopeUtils.DEFAULT_SCOPE, callback);
    }

    public void getObject(String type, String id, String scope, FutureCallback<ObjectDO> callback) {
        ListenableFuture<ObjectDO> future = objectAccessor.getObject(id, type, scope);

        Futures.addCallback(future, new FutureCallback<ObjectDO>() {
            @Override
            public void onSuccess(ObjectDO objectDO) {
                callback.onSuccess(objectDO);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}

@Accessor
interface ObjectAccessor {
    @Query("SELECT * FROM omnistore.objects WHERE id = :id and type = :type and scope = :scope")
    ListenableFuture<ObjectDO> getObject(String id, String type, String scope);


    @Query("INSERT INTO omnistore.objects (id, type, scope, created, last_updated, properties) VALUES " +
            "(:id, :type, :scope, dateof(now()), dateof(now()), :properties)")
    ListenableFuture<ResultSet> putObject(String id, String type, String scope, String properties);

    @Query("DELETE FROM omnistore.objects WHERE id = :id and type = :type and scope = :scope")
    ListenableFuture<ResultSet> deleteObject(String id, String type, String scope);
}

