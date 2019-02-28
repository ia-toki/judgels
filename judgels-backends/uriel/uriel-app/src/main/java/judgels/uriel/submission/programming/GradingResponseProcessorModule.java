package judgels.uriel.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.time.Clock;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.sandalphon.submission.programming.GradingResponsePoller;
import judgels.sandalphon.submission.programming.GradingResponseProcessor;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.sealtiel.api.message.MessageService;
import judgels.service.api.client.BasicAuthHeader;

@Module
public class GradingResponseProcessorModule {
    private GradingResponseProcessorModule() {}

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            LifecycleEnvironment lifecycleEnvironment,
            Clock clock,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService,
            GradingResponseProcessor processor) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("grading-response-processor-%d")
                        .maxThreads(10)
                        .minThreads(10)
                        .build();

        return new GradingResponsePoller(clock, sealtielClientAuthHeader, messageService, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponseProcessor contestScoreboardUpdater(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            @Named("sealtiel") BasicAuthHeader sealtielClientAuthHeader,
            MessageService messageService) {

        return unitOfWorkAwareProxyFactory.create(
                GradingResponseProcessor.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        SubmissionStore.class,
                        BasicAuthHeader.class,
                        MessageService.class},
                new Object[] {
                        mapper,
                        submissionStore,
                        sealtielClientAuthHeader,
                        messageService});
    }
}
