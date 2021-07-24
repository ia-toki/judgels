package judgels.uriel.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.messaging.MessageClient;
import judgels.messaging.rabbitmq.RabbitMQ;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;

@Module
public class MessagingModule {
    private final RabbitMQConfiguration config;

    public MessagingModule(Optional<RabbitMQConfiguration> config) {
        this.config = config.orElse(RabbitMQConfiguration.DEFAULT);
    }

    @Provides
    @Singleton
    MessageClient messageClient(ObjectMapper objectMapper) {
        return new MessageClient(new RabbitMQ(config), objectMapper);
    }
}
