package com.cyngn.omnistore;

import com.cyngn.omnistore.api.GraphApi;
import com.cyngn.omnistore.config.ServerConfig;
import com.cyngn.omnistore.registry.TypeRegistry;
import com.cyngn.vertx.eventbus.EventBusTools;
import com.datastax.driver.core.Cluster;
import com.englishtown.vertx.cassandra.impl.DefaultCassandraSession;
import com.englishtown.vertx.cassandra.impl.JsonCassandraConfigurator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Server, starts everything up.
 *
 * to build:
 * ./gradlew fatjar
 *
 * to run:
 * java -jar build/libs/omnistore-1.0.0-fat.jar -conf conf.json -instances 2
 *
 * NOTE: start a cassandra instance on localhost with 'bin/cassandra' before running server
 *
 * @author asarda@cyngn.com (Ajay Sarda) 8/14/2015
 */
public class Server extends AbstractVerticle  {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    // local shared map, shared across all vert.x instances
    private LocalMap<String, Long> sharedData;

    private final String SHARED_DATA = "shared_data";
    public static final String INITIALIZER_THREAD_KEY = "InitializerThread";

    private ServerConfig svrConfig = null;

    private HttpServer server;
    private DefaultCassandraSession session;
    private static String INITIALIZED_MSG = "server.initialized";

    private TypeRegistry registry;

    @Override
    public void start(final Future<Void> startedResult) {
        try {
            JsonObject config = config();
            svrConfig = new ObjectMapper().readValue(config.toString(), ServerConfig.class);
            svrConfig.cassandra = config.getJsonObject("cassandra");
        } catch (IOException e) {
            logger.error("Failed to load the server config", e);
            stop();
        }

        // attempt to become the initializer thread
        sharedData = vertx.sharedData().getLocalMap(SHARED_DATA);
        sharedData.putIfAbsent(INITIALIZER_THREAD_KEY, Thread.currentThread().getId());

        Router router = Router.router(vertx);

        // initialize the storage
        JsonCassandraConfigurator configurator = new JsonCassandraConfigurator(vertx);
        session = new DefaultCassandraSession(Cluster.builder(), configurator, vertx);

        // listen for the initialized message, the sending thread receives this also
        EventBusTools.oneShotLocalConsumer(vertx.eventBus(), INITIALIZED_MSG, getStartupHandler());

        if (isInitializerThread()) {
            //initialize the type registry
            try {
                svrConfig.registry = config().getJsonObject("registry");
                registry = new TypeRegistry(svrConfig.registry);
                vertx.eventBus().publish(INITIALIZED_MSG, new JsonObject());
            } catch (Exception e) {
                logger.error("Failed to initialized registry", e);
                stop();
            }
        }

    }

    protected final boolean isInitializerThread() {
        return sharedData.get(INITIALIZER_THREAD_KEY) == Thread.currentThread().getId();
    }

    /**
     * Handles starting the server listening on all interfaces
     *
     * @return a callback to hit when ready for the server to open up listening
     */
    private Handler<Message<Object>> getStartupHandler() {
        return message -> {
            server = vertx.createHttpServer();
            Router router = Router.router(vertx);

            GraphApi graphApi = new GraphApi(session, vertx, registry);

            if (isInitializerThread()) {
                graphApi.outputApi(logger);
            }
            graphApi.init(router);

            server.requestHandler(router::accept);

            server.listen(svrConfig.port, "0.0.0.0", event -> {
                if (event.failed()) {
                    logger.error("Failed to start server, error:", event.cause());
                    stop();
                } else { logger.info("Thread: {} starting to handle request", Thread.currentThread().getId()); }
            });
        };
    }

    @Override
    public void stop() {
        logger.info("Stopping the server");
        try {
            if(server != null) { server.close(); }
        } finally {
            // Only one thread can close the vertx as vertx is shared across all instances.
            Long shutdownThreadId = sharedData.putIfAbsent("shutdown", Thread.currentThread().getId());
            if (shutdownThreadId == null) {
                vertx.close(event -> {
                    logger.info("Vertx instance closed");
                    System.exit(-1);
                });
            }
        }
    }

}
