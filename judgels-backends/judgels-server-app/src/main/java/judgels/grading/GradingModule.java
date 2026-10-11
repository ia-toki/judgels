package judgels.grading;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.core.JudgelsBaseDataDir;
import judgels.core.JudgelsScheduler;
import judgels.core.messaging.MessageClient;
import judgels.core.messaging.MessageListener;

@Module
public final class GradingModule {
    private final JudgelsServerGradingConfiguration config;

    public GradingModule(JudgelsServerGradingConfiguration config) {
        this.config = config;
    }

    @Provides
    @Named("gradingRequestQueueName")
    String gradingRequestQueueName() {
        return config.getGradingRequestQueueName();
    }

    @Provides
    @Named("gradingResponseQueueName")
    String gradingResponseQueueName() {
        return config.getGradingResponseQueueName();
    }

    @Provides
    @Singleton
    FakeGradingRequestPoller fakeGradingRequestPoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            MessageClient messageClient,
            @JudgelsBaseDataDir Path baseDataDir) {

        return new FakeGradingRequestPoller(
                messageListener,
                messageClient,
                config.getGradingRequestQueueName(),
                (ThreadPoolExecutor) scheduler.createExecutorService("fake-grading-request-processor-%d", 2),
                baseDataDir,
                new FakeGrader());
    }
}
