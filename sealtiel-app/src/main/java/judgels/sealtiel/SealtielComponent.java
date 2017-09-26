package judgels.sealtiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.sealtiel.message.MessageResource;
import judgels.sealtiel.rabbitmq.RabbitMQHealthCheck;
import judgels.sealtiel.rabbitmq.RabbitMQModule;

@Component(modules = {
        SealtielModule.class,
        RabbitMQModule.class})
@Singleton
public interface SealtielComponent {
    MessageResource messageResource();
    RabbitMQHealthCheck rabbitmqHealthCheck();
}
