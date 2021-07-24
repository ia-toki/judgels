package judgels.gabriel.grading;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.util.concurrent.ExecutorService;
import javax.inject.Provider;
import javax.inject.Singleton;
import judgels.messaging.MessageClient;

@Module
public class GradingModule {
    private final GradingConfiguration gradingConfig;

    public GradingModule(GradingConfiguration config) {
        this.gradingConfig = config;
    }

    @Provides
    GradingConfiguration config() {
        return gradingConfig;
    }

    @Provides
    @Singleton
    GradingRequestPoller gradingRequestPoller(
            MessageClient messageClient,
            Provider<GradingWorker> workerFactory,
            LifecycleEnvironment lifecycleEnvironment) {

        ExecutorService executorService = lifecycleEnvironment.executorService("grading-worker-%d")
                .maxThreads(gradingConfig.getNumWorkerThreads())
                .minThreads(gradingConfig.getNumWorkerThreads())
                .build();

        return new GradingRequestPoller(
                executorService,
                gradingConfig.getGradingRequestQueueName(),
                messageClient,
                workerFactory);
    }
}
