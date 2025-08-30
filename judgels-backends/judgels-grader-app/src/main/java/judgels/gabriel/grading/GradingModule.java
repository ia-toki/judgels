package judgels.gabriel.grading;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.messaging.MessageListener;
import judgels.service.JudgelsBaseDataDir;

@Module
public class GradingModule {
    private final LifecycleEnvironment lifecycleEnv;
    private final GradingConfiguration config;

    public GradingModule(LifecycleEnvironment lifecycleEnv, GradingConfiguration config) {
        this.lifecycleEnv = lifecycleEnv;
        this.config = config;
    }

    @Provides
    GradingConfiguration config() {
        return config;
    }

    @Provides
    @Named("workersDir")
    static Path gradingWorkersDir(@JudgelsBaseDataDir Path baseDataDir) {
        return baseDataDir.resolve("workers");
    }

    @Provides
    @Singleton
    GradingRequestPoller gradingRequestPoller(
            MessageListener messageListener,
            Provider<GradingWorker> workerFactory) {

        ExecutorService executorService = lifecycleEnv.executorService("grading-worker-%d")
                .maxThreads(config.getNumWorkerThreads())
                .minThreads(config.getNumWorkerThreads())
                .build();

        return new GradingRequestPoller(
                messageListener,
                (ThreadPoolExecutor) executorService,
                config.getGradingRequestQueueName(),
                workerFactory);
    }
}
