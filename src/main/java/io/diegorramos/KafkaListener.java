package io.diegorramos;

import io.helidon.config.Config;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConfigBuilder;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import io.helidon.websocket.WsListener;
import io.helidon.websocket.WsSession;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class KafkaListener implements WsListener {

    private static final Logger LOGGER = Logger.getLogger(KafkaListener.class.getName());

    private final Config config = Config.create();

    private final Map<WsSession, Messaging> messagingRegister = new HashMap<>();

    @Override
    public void onOpen(WsSession session) {
        String kafkaServer = config.get("app.kafka.bootstrap.servers").asString().get();
        String topic = config.get("app.kafka.topic").asString().get();
        String compression = config.get("app.kafka.compression").asString().orElse("none");

        Channel<String> fromKafka = Channel.<String>builder()
                .name("from-kafka")
                .publisherConfig(KafkaConnector.configBuilder()
                        .bootstrapServers(kafkaServer)
                        .groupId("example-group-" + session)
                        .topic(topic)
                        .autoOffsetReset(KafkaConfigBuilder.AutoOffsetReset.LATEST)
                        .enableAutoCommit(true)
                        .keyDeserializer(StringDeserializer.class)
                        .valueDeserializer(StringDeserializer.class)
                        .compressionType(compression)
                        .build()
                )
                .build();

        // Prepare Kafka connector, can be used by any channel
        KafkaConnector kafkaConnector = KafkaConnector.create();

        Messaging messaging = Messaging.builder()
                .connector(kafkaConnector)
                .listener(fromKafka, payload -> {
                    System.out.println("Kafka says: " + payload);
                    // Send message received from Kafka over websocket
                    session.send(payload, true);
                })
                .build()
                .start();

        //Save the messaging instance for proper shutdown
        // when websocket connection is terminated
        messagingRegister.put(session, messaging);
    }

    @Override
    public void onClose(WsSession session, int status, String reason) {
        LOGGER.info("Closing session " + session);
        // Properly stop messaging when websocket connection is terminated
        Optional.ofNullable(messagingRegister.remove(session))
                .ifPresent(Messaging::stop);
    }
}
