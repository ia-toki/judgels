package judgels.sealtiel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.sealtiel.message.MessageResource;
import judgels.sealtiel.rabbitmq.RabbitMQHealthCheck;
import judgels.sealtiel.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        JudgelsModule.class,
        RabbitMQModule.class,
        SealtielModule.class})
@Singleton
public interface SealtielComponent {
    MessageResource messageResource();
    RabbitMQHealthCheck rabbitmqHealthCheck();
    PingResource pingResource();
}
