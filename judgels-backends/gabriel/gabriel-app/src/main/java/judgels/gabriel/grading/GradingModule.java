package judgels.gabriel.grading;

import dagger.Module;
import dagger.Provides;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;

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
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            Provider<GradingWorker> workerFactory,
            LifecycleEnvironment lifecycleEnvironment) {

        ExecutorService executorService = lifecycleEnvironment.executorService("grading-worker-%d")
                .maxThreads(gradingConfig.getNumWorkerThreads())
                .minThreads(gradingConfig.getNumWorkerThreads())
                .build();

        return new GradingRequestPoller(
                executorService,
                sealtielClientAuthHeader,
                messageService,
                workerFactory);
    }
}
