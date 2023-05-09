package judgels.sandalphon.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.JudgelsBaseDataDir;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.sandalphon.persistence.ProgrammingGradingDao;
import judgels.sandalphon.persistence.ProgrammingSubmissionDao;
import judgels.sandalphon.problem.base.submission.SubmissionFs;
import judgels.sandalphon.submission.programming.BaseSubmissionStore;
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
    private SubmissionModule() {}

    @Provides
    @Singleton
    @SubmissionFs
    static FileSystem submissionFs(@JudgelsBaseDataDir Path baseDataDir) {
        return new LocalFileSystem(baseDataDir.resolve("submissions"));
    }

    @Provides
    @Singleton
    static SubmissionStore submissionStore(
            ObjectMapper objectMapper,
            ProgrammingSubmissionDao submissionDao,
            ProgrammingGradingDao gradingDao) {
        return new BaseSubmissionStore<>(submissionDao, gradingDao, objectMapper);
    }

    @Provides
    @Singleton
    static SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    static SubmissionClient submissionClient(
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
    static SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("sandalphon-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageClient messageClient,
            GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("sandalphon-grading-response-processor-%d", 10);
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
