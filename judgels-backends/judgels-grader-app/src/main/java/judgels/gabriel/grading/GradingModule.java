package judgels.gabriel.grading;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import judgels.messaging.MessageClient;

@Module
public class GradingModule {
    private final String baseDataDir;
    private final LifecycleEnvironment lifecycleEnv;
    private final GradingConfiguration config;

    public GradingModule(String baseDataDir, LifecycleEnvironment lifecycleEnv, GradingConfiguration config) {
        this.baseDataDir = baseDataDir;
        this.lifecycleEnv = lifecycleEnv;
        this.config = config;
    }

    @Provides
    GradingConfiguration config() {
        return config;
    }

    @Provides
    @Named("workersDir")
    Path gradingWorkersDir() {
        return Paths.get(baseDataDir, "workers");
    }

    @Provides
    @Named("problemsDir")
    Path gradingProblemsDir() {
        return Paths.get(baseDataDir, "problems");
    }

    @Provides
    @Singleton
    GradingRequestPoller gradingRequestPoller(
            MessageClient messageClient,
            Provider<GradingWorker> workerFactory) {

        ExecutorService executorService = lifecycleEnv.executorService("grading-worker-%d")
                .maxThreads(config.getNumWorkerThreads())
                .minThreads(config.getNumWorkerThreads())
                .build();

        return new GradingRequestPoller(
                executorService,
                config.getGradingRequestQueueName(),
                messageClient,
                workerFactory);
    }
}
