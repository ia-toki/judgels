package judgels.core.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class RabbitMQModule {
    private final RabbitMQConfiguration config;
    private final boolean enabled;
    private final FakeRabbitMQ fakeRabbitMQ = new FakeRabbitMQ();

    public RabbitMQModule(RabbitMQConfiguration config) {
        this.config = config;
        this.enabled = true;
    }

    public RabbitMQModule(Optional<RabbitMQConfiguration> config) {
        this.config = config.orElse(RabbitMQConfiguration.DEFAULT);
        this.enabled = config.isPresent();
    }

    @Provides
    @Singleton
    RabbitMQ rabbitMQ() {
        return new RabbitMQ(config);
    }

    @Provides
    @Singleton
    MessageClient messageClient(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        return new MessageClient(objectMapper, enabled ? Optional.of(rabbitMQ) : Optional.empty(), fakeRabbitMQ);
    }

    @Provides
    MessageListener messageListener(ObjectMapper objectMapper, RabbitMQ rabbitMQ) {
        return new MessageListener(objectMapper, enabled ? Optional.of(rabbitMQ) : Optional.empty(), fakeRabbitMQ);
    }
}
