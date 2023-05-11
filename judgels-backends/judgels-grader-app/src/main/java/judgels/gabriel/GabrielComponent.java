package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        JudgelsModule.class,
        GabrielModule.class,

        RabbitMQModule.class,
        IsolateModule.class,

        GradingModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
