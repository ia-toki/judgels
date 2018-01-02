package judgels.sealtiel.rabbitmq;

import com.codahale.metrics.health.HealthCheck;
import com.rabbitmq.http.client.Client;
import java.net.URL;
import javax.inject.Inject;

public class RabbitMQHealthCheck extends HealthCheck {
    private final RabbitMQConfiguration config;

    @Inject
    public RabbitMQHealthCheck(RabbitMQConfiguration config) {
        this.config = config;
    }

    @Override
    protected Result check() throws Exception {
        URL url = new URL("http", config.getHost(), config.getManagementPort(), "/api/");
        Client client = new Client(url, config.getUsername(), config.getPassword());

        if (client.alivenessTest(config.getVirtualHost())) {
            return Result.healthy();
        } else {
            return Result.unhealthy("RabbitMQ node is not alive");
        }
    }
}
