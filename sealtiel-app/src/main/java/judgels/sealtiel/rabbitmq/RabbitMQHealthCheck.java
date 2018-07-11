package judgels.sealtiel.rabbitmq;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.codahale.metrics.health.HealthCheck;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.client.JerseyClientBuilder;

public class RabbitMQHealthCheck extends HealthCheck {
    private final RabbitMQConfiguration config;

    @Inject
    public RabbitMQHealthCheck(RabbitMQConfiguration config) {
        this.config = config;
    }

    @Override
    protected Result check() throws Exception {
        String url = "http://" + config.getHost() + ":" + config.getManagementPort() + "/api/healthchecks/node";
        String creds = config.getUsername() + ":" + config.getPassword();
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(creds.getBytes());

        Client client = new JerseyClientBuilder().build();

        Map<String, String> result = client
                .target(url)
                .request(APPLICATION_JSON)
                .header(AUTHORIZATION, authHeader)
                .get()
                .readEntity(new GenericType<HashMap<String, String>>() {});

        if (result.get("status").equals("ok")) {
            return Result.healthy();
        } else {
            return Result.unhealthy(result.get("reason"));
        }
    }
}
