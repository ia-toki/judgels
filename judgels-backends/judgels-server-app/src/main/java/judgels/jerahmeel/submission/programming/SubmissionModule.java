package judgels.jerahmeel.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;
import judgels.jerahmeel.JerahmeelBaseDataDir;
import judgels.jerahmeel.stats.StatsConfiguration;
import judgels.messaging.MessageClient;
import judgels.sandalphon.submission.programming.GradingResponsePoller;
import judgels.sandalphon.submission.programming.GradingResponseProcessor;
import judgels.sandalphon.submission.programming.NoOpSubmissionConsumer;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionConsumer;
import judgels.sandalphon.submission.programming.SubmissionRegradeProcessor;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsScheduler;

@Module
public class SubmissionModule {
    private final SubmissionConfiguration config;
    private final StatsConfiguration statsConfig;

    public SubmissionModule(SubmissionConfiguration config, StatsConfiguration statsConfig) {
        this.config = config;
        this.statsConfig = statsConfig;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig, @JerahmeelBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("submissions"));
    }

    @Provides
    @Singleton
    SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    SubmissionClient submissionClient(
            SubmissionStore submissionStore,
            @Named("gradingRequestQueueName") String gradingRequestQueueName,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageClient messageClient,
            ObjectMapper mapper) {

        return new SubmissionClient(
                submissionStore,
                gradingRequestQueueName,
                gradingResponseQueueName,
                messageClient,
                mapper);
    }

    @Provides
    @Singleton
    SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("jerahmeel-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageClient messageClient,
            GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("grading-response-processor-%d", 10);
        return new GradingResponsePoller(gradingResponseQueueName, messageClient, executorService, processor);
    }

    @Provides
    @Singleton
    GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            MessageClient messageClient,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                GradingResponseProcessor.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        SubmissionStore.class,
                        MessageClient.class,
                        SubmissionConsumer.class},
                new Object[] {
                        mapper,
                        submissionStore,
                        messageClient,
                        statsConfig.getEnabled() ? statsProcessor : new NoOpSubmissionConsumer()});
    }
}
