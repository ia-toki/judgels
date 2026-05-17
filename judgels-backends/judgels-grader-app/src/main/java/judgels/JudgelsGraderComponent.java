package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.grading.CacheModule;
import judgels.grading.GradingModule;
import judgels.grading.GradingRequestPoller;
import judgels.isolate.IsolateModule;
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
        GradingModule.class,
        CacheModule.class})
@Singleton
public interface JudgelsGraderComponent {
    GradingRequestPoller gradingRequestPoller();
}
