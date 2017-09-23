package judgels.sealtiel.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import judgels.sealtiel.queue.Queue;
import judgels.sealtiel.queue.QueueChannel;

public class RabbitMQ implements Queue {
    private final ConnectionFactory connectionFactory;

    private volatile Connection connection;
    private volatile Channel channel;

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
        }
        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
        }
        return new RabbitMQChannel(channel);
    }
}
