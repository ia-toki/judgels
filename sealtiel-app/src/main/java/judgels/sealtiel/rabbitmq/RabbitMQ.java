package judgels.sealtiel.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sealtiel.queue.Queue;
import judgels.sealtiel.queue.QueueChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class RabbitMQ implements Queue {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQ.class);

    private final ConnectionFactory connectionFactory;

    private volatile Connection connection;
    private volatile Channel channel;

    @Inject
    public RabbitMQ(RabbitMQConfiguration config) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setPort(config.getPort());
        factory.setUsername(config.getUsername());
        factory.setPassword(config.getPassword());
        factory.setVirtualHost(config.getVirtualHost());

        this.connectionFactory = factory;
    }

    @Override
    public synchronized QueueChannel createChannel() throws IOException, TimeoutException {
        if (connection == null || !connection.isOpen()) {
            connection = connectionFactory.newConnection();
            LOGGER.info("Created a new connection to RabbitMQ");
        }
        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
            LOGGER.info("Created a new channel to RabbitMQ");
        }
        return new RabbitMQChannel(channel);
    }
}
