package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.moe.MoeModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        JudgelsModule.class,
        GabrielModule.class,

        RabbitMQModule.class,
        MoeModule.class,

        GradingModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
