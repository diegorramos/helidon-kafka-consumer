package io.diegorramos;

import io.helidon.webserver.testing.junit5.DirectClient;
import io.helidon.webserver.testing.junit5.RoutingTest;

@RoutingTest
class KafkaConsumerApplicationTest extends AbstractKafkaConsumerApplicationTest {
    KafkaConsumerApplicationTest(DirectClient client) {
        super(client);
    }
}