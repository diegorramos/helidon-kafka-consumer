
package io.diegorramos;


import io.helidon.logging.common.LogConfig;
import io.helidon.config.Config;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.websocket.WsRouting;


/**
 * The application main class.
 */
public class KafkaConsumerApplication {


    /**
     * Cannot be instantiated.
     */
    private KafkaConsumerApplication() {}


    /**
     * Application main entry point.
     *
     * @param args command line arguments.
     */
    public static void main(String[] args) {

        // load logging configuration
        LogConfig.configureRuntime();

        // initialize global config from default configuration
        Config config = Config.create();
        Config.global(config);


        WebServer server = WebServer.builder()
                .config(config.get("server"))
                .routing(KafkaConsumerApplication::routing)
                .addRouting(WsRouting.builder()
                        .endpoint("/ws/messages", new KafkaListener()))
                .build()
                .start();


        System.out.println("WEB server is up! http://localhost:" + server.port() + "/simple-greet");

    }


    /**
     * Updates HTTP Routing.
     */
    static void routing(HttpRouting.Builder routing) {
        routing
                .register("/greet", new GreetService())
                .get("/simple-greet", (req, res) -> res.send("Hello World!"));
    }
}