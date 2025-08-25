package judgels.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.messaging.MessageClient;
import judgels.messaging.MessageListener;

@Module
public class RabbitMQModule {
    private final RabbitMQConfiguration config;

    public RabbitMQModule(RabbitMQConfiguration config) {
        this.config = config;
    }

    public RabbitMQModule(Optional<RabbitMQConfiguration> config) {
        this.config = config.orElse(RabbitMQConfiguration.DEFAULT);
    }

    @Provides
    @Singleton
    RabbitMQ rabbitMQ() {
        return new RabbitMQ(config);
    }

    @Provides
    @Singleton
    MessageClient messageClient(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        return new MessageClient(objectMapper, rabbitMQ);
    }

    @Provides
    MessageListener messageListener(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        return new MessageListener(objectMapper, rabbitMQ);
    }
}
