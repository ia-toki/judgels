package judgels.messaging.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQ implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQ.class);
    private static final Object connectionLock = new Object();

    private static volatile Connection connection;

    private final ConnectionFactory connectionFactory;

    public RabbitMQ(RabbitMQConfiguration config) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setPort(5672);
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        factory.setVirtualHost("/");

        this.connectionFactory = factory;
    }

    public Connection getConnection() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            synchronized (connectionLock) {
                if (connection == null || !connection.isOpen()) {
                    LOGGER.info("Creating a new connection to RabbitMQ...");
                    connection = connectionFactory.newConnection();
                    LOGGER.info("Created a new connection to RabbitMQ");
                }
            }
        }
        return connection;
    }

    @Override
    public void close() throws IOException {
        synchronized (connectionLock) {
            if (connection != null && connection.isOpen()) {
                LOGGER.info("Closing the connection to RabbitMQ...");
                connection.close();
                LOGGER.info("Closed the connection to RabbitMQ");
            }
        }
    }
}
