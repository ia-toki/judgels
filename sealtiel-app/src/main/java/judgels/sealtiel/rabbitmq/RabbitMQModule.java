package judgels.sealtiel.rabbitmq;

import dagger.Module;
import dagger.Provides;
import judgels.sealtiel.queue.Queue;

@Module
public class RabbitMQModule {
    private final RabbitMQConfiguration config;

    public RabbitMQModule(RabbitMQConfiguration config) {
        this.config = config;
    }

    @Provides
    public Queue queue() {
        return new RabbitMQ(config);
    }
}
