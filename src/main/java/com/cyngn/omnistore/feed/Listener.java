package com.cyngn.omnistore.feed;

import com.cyngn.omnistore.storage.ConnectionStorage;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;

/**
 * Listener for listening on connection updates on event bus and update
 * the feed list in redis.
 *
 * @author asarda@cyngn.com (Ajay Sarda) on 8/15/15.
 */
public class Listener {
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);
    private RedisClient redisClient;
    private ConnectionStorage storage;

    public Listener(Vertx vertx, JsonObject redisConfig, ConnectionStorage connectionStorage) {
        vertx.eventBus().consumer("feed", new Handler<Message<Object>>() {
            @Override
            public void handle(Message<Object> event) {
                String message = event.body().toString();
                processMessage(new JsonObject(message));

            }
        });

        redisClient = RedisClient.create(vertx, redisConfig);

    }

    private void processMessage(JsonObject messageJson) {
        logger.info("processing the message " + messageJson.toString());

        // look up friends/followers for the user.
        // add the event to the head of the list.

        String srcId = messageJson.getString("src_id");
        String srcType = messageJson.getString("src_type");

    }
}
