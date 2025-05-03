package judgels.gabriel;

import dagger.Component;
import jakarta.inject.Singleton;
import judgels.gabriel.cache.CacheModule;
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
        GradingModule.class,
        CacheModule.class})
@Singleton
public interface GabrielComponent {
    GradingRequestPoller gradingRequestPoller();
}
