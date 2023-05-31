package judgels.gabriel;

import dagger.Component;
import javax.inject.Singleton;
import judgels.gabriel.grading.GradingModule;
import judgels.gabriel.grading.GradingRequestPoller;
import judgels.gabriel.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;
import judgels.service.JudgelsModule;

@Component(modules = {
        // Judgels service
        JudgelsModule.class,
        JudgelsGraderModule.class,

        // 3rd parties
        RabbitMQModule.class,
        IsolateModule.class,

        // Features
        GradingModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
