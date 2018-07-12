package judgels.sealtiel.rabbitmq;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.sealtiel.queue.Queue;

@Module
public class RabbitMQModule {
    private final RabbitMQConfiguration config;

    public RabbitMQModule(RabbitMQConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    RabbitMQConfiguration rabbitMQConfig() {
        return config;
    }

    @Provides
    Queue queue(RabbitMQ rabbitMQ) {
        return rabbitMQ;
    }
}
