package judgels.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.messaging.MessageClient;

@Module
public class RabbitMQModule {
    private final RabbitMQConfiguration config;

    public RabbitMQModule(Optional<RabbitMQConfiguration> config) {
        this.config = config.orElse(RabbitMQConfiguration.DEFAULT);
    }

    @Provides
    @Singleton
    MessageClient messageClient(ObjectMapper objectMapper) {
        return new MessageClient(new RabbitMQ(config), objectMapper);
    }
}
