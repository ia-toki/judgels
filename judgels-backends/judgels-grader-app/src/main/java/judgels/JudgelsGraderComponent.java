package judgels;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.core.JudgelsModule;
import judgels.grading.GradingModule;
import judgels.grading.GradingRequestPoller;
import judgels.grading.cache.CacheModule;
import judgels.isolate.IsolateModule;
import judgels.messaging.rabbitmq.RabbitMQModule;

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
