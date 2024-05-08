package io.diegorramos;

import io.helidon.webclient.http1.Http1Client;
import io.helidon.webserver.testing.junit5.ServerTest;

@ServerTest
class KafkaConsumerApplicationIT extends AbstractKafkaConsumerApplicationTest {
    KafkaConsumerApplicationIT(Http1Client client) {
        super(client);
    }
}