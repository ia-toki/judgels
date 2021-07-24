package judgels.uriel.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;
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
import judgels.uriel.UrielBaseDataDir;

@Module
public class SubmissionModule {
    private final SubmissionConfiguration config;

    public SubmissionModule(SubmissionConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig, @UrielBaseDataDir Path baseDataDir) {
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
            LifecycleEnvironment lifecycleEnvironment,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("submission-regrade-processor-%d")
                        .maxThreads(5)
                        .minThreads(5)
                        .build();

        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            LifecycleEnvironment lifecycleEnvironment,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageClient messageClient,
            GradingResponseProcessor processor) {

        ExecutorService executorService =
                lifecycleEnvironment.executorService("grading-response-processor-%d")
                        .maxThreads(10)
                        .minThreads(10)
                        .build();

        return new GradingResponsePoller(gradingResponseQueueName, messageClient, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            SubmissionStore submissionStore,
            MessageClient messageClient) {

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
                        new NoOpSubmissionConsumer()});
    }
}
