package com.cyngn.omnistore.storage;

import com.cyngn.omnistore.registry.TypeRegistry;
import com.cyngn.omnistore.storage.dataobjects.ConnectionDO;
import com.cyngn.vertx.async.promise.Promise;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.englishtown.vertx.cassandra.CassandraSession;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.vertx.core.Vertx;

/**
 * Implementation for cassandra based connection storage
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/14/15.
 */
public class ConnectionStorage {

    private final ConnectionAccessor connectionAccessor;
    private final Vertx vertx;

    private final TypeRegistry registry;

    public ConnectionStorage(CassandraSession session, Vertx vertx, TypeRegistry registry) {
        this.vertx = vertx;
        MappingManager manager = new MappingManager(session.getSession());
        connectionAccessor = manager.createAccessor(ConnectionAccessor.class);
        this.registry = registry;
    }

    public void getConnection(String srcType, String srcId, String scope, String type, String dstType, String dstId, FutureCallback<Result<ConnectionDO>> callback) {
        ListenableFuture<Result<ConnectionDO>> future = connectionAccessor.getConnection(srcId, srcType, scope, type, dstId, dstType);

        Futures.addCallback(future, new FutureCallback<Result<ConnectionDO>>() {
            @Override
            public void onSuccess(Result<ConnectionDO> result) { callback.onSuccess(result); }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }


    public void getConnections(String type, String id, String scope, String connType, FutureCallback<Result<ConnectionDO>> callback) {
        ListenableFuture<Result<ConnectionDO>> future;

        if (registry.getConnectionType(connType).isMulti()) {
            future = connectionAccessor.getMultiConnections(id, type, scope, connType);
        } else {
            future = connectionAccessor.getConnections(id, type, scope, connType);
        }

        Futures.addCallback(future, new FutureCallback<Result<ConnectionDO>>() {
            @Override
            public void onSuccess(Result<ConnectionDO> result) { callback.onSuccess(result); }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void getAllConnections(String id, String type, String scope, FutureCallback<Result<ConnectionDO>> callback) {
        ListenableFuture<Result<ConnectionDO>> future = connectionAccessor.getConnections(id, type, scope);

        Futures.addCallback(future, new FutureCallback<Result<ConnectionDO>>() {
            @Override
            public void onSuccess(Result<ConnectionDO> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public void putConnection(String srcId, String srcType, String scope, String type, String dstId, String dstType, String properties, FutureCallback<ResultSet> callback) {
        ListenableFuture<ResultSet> future;

        if (registry.getConnectionType(type).isMulti()) {
            future = connectionAccessor.putMultiConnection(srcId, srcType, scope, type, dstId, dstType, properties);
        } else {
            future = connectionAccessor.putConnection(srcId, srcType, scope, type, dstId, dstType, properties);
        }

        Promise promise = Promise.newInstance(vertx);

        promise.then((context, onResult) -> {
            Futures.addCallback(future, new FutureCallback<ResultSet>() {
                @Override
                public void onSuccess(ResultSet result) {
                    callback.onSuccess(result);
                    onResult.accept(true);
                }

                @Override
                public void onFailure(Throwable t) {
                    callback.onFailure(t);
                    onResult.accept(false);
                }
            });
        }).then((context1, onResult) -> {
            ListenableFuture<ResultSet> reverseFuture;
            // if we need to write the reverse connection
            if (!registry.getConnectionType(type).directed) {
                if (registry.getConnectionType(type).isMulti()) {
                    reverseFuture = connectionAccessor.putMultiConnection(dstId, dstType, scope, type, srcId, srcType, properties);
                } else {
                    reverseFuture = connectionAccessor.putConnection(dstId, dstType, scope, type, srcId, srcType, properties);
                }

                Futures.addCallback(reverseFuture, new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess(ResultSet result) {
                        callback.onSuccess(result);
                        onResult.accept(true);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onFailure(t);
                        onResult.accept(false);
                    }
                });

            }
        }).eval();

    }

    public void deleteConnection(String srcType, String srcId, String scope, String type, String dstType, String dstId, FutureCallback<ResultSet> callback) {
        ListenableFuture<ResultSet> future = connectionAccessor.deleteConnection(srcId, srcType, scope, type, dstId, dstType);

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

    public void deleteConnection(String type, String id, String scope, FutureCallback<ResultSet> callback) {
        ListenableFuture<ResultSet> future = connectionAccessor.deleteConnection(id, type, scope);

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
}

@Accessor
interface ConnectionAccessor {
    @Query("INSERT INTO omnistore.connections (src_id, src_type, scope, type, dst_id, dst_type, created, last_updated, properties) " +
            "VALUES (:srcId, :srcType, :type, :dstId, :dstType, :scope, dateof(now()), dateof(now()), :properties)")
    ListenableFuture<ResultSet> putConnection(String srcId, String srcType, String scope, String type, String dstId, String dstType, String properties);

    @Query("SELECT * FROM omnistore.connections WHERE src_id = :srcId and " +
            "src_type = :srcType and scope = :scope and type = :type and dst_id = :dstId and dst_type = :dstType")
    ListenableFuture<Result<ConnectionDO>> getConnection(String srcId, String srcType, String scope, String type, String dstId, String dstType);

    @Query("SELECT * FROM omnistore.connections WHERE src_id = :srcId and src_type = :srcType and scope = :scope")
    ListenableFuture<Result<ConnectionDO>> getConnections(String srcId, String srcType, String scope);

    @Query("SELECT * FROM omnistore.connections WHERE src_id = :srcId and src_type = :srcType and scope =:scope and type = :type ")
    ListenableFuture<Result<ConnectionDO>> getConnections(String srcId, String srcType, String scope, String type);


    @Query("INSERT INTO omnistore.multi_connections (src_id, src_type, scope, type, dst_id, dst_type, created, last_updated, properties) " +
            "VALUES (:srcId, :srcType, :type, :dstId, :dstType, :scope, dateof(now()), dateof(now()), :properties)")
    ListenableFuture<ResultSet> putMultiConnection(String srcId, String srcType, String scope, String type, String dstId, String dstType, String properties);

    @Query("SELECT * FROM omnistore.multi_connections WHERE src_id = :srcId and src_type = :srcType and scope = :scope")
    ListenableFuture<Result<ConnectionDO>> getMultiConnections(String srcId, String srcType, String scope);

    @Query("SELECT * FROM omnistore.multi_connections WHERE src_id = :srcId and src_type = :srcType and scope =:scope and type = :type ")
    ListenableFuture<Result<ConnectionDO>> getMultiConnections(String srcId, String srcType, String scope, String type);

    @Query("DELETE FROM omnistore.connections WHERE src_id = :srcId and src_type = :srcType and scope = :scope")
    ListenableFuture<ResultSet> deleteConnection(String srcId, String srcType, String scope);

    @Query("DELETE FROM omnistore.connections WHERE src_id = :srcId and src_type = :srcType and scope = :scope and type = :type and dst_id = :dstId and dst_type = :dstType")
    ListenableFuture<ResultSet> deleteConnection(String srcId, String srcType, String scope, String type, String dstId, String dstType);
}



