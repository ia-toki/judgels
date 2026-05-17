package judgels.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.messaging.MessageListener;
import judgels.persistence.ProgrammingGradingDao;
import judgels.persistence.ProgrammingSubmissionDao;
import judgels.problem.base.submission.SubmissionFs;
import judgels.service.JudgelsBaseDataDir;
import judgels.service.JudgelsScheduler;
import judgels.submission.programming.BaseSubmissionStore;
import judgels.submission.programming.GradingResponsePoller;
import judgels.submission.programming.GradingResponseProcessor;
import judgels.submission.programming.NoOpSubmissionConsumer;
import judgels.submission.programming.SubmissionClient;
import judgels.submission.programming.SubmissionConsumer;
import judgels.submission.programming.SubmissionRegradeProcessor;
import judgels.submission.programming.SubmissionRegrader;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import org.hibernate.SessionFactory;

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
            SessionFactory sessionFactory,
            SubmissionStore submissionStore,
            @Named("gradingRequestQueueName") String gradingRequestQueueName,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageClient messageClient,
            ObjectMapper mapper) {

        return new SubmissionClient(
                sessionFactory,
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
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient) {

        ExecutorService executorService = scheduler.createExecutorService("sandalphon-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(
                submissionStore,
                executorService,
                new SubmissionRegradeProcessor(submissionSourceBuilder, submissionClient));
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("sandalphon-grading-response-processor-%d", 10);
        return new GradingResponsePoller(
                messageListener,
                gradingResponseQueueName,
                (ThreadPoolExecutor) executorService,
                processor);
    }

    @Provides
    @Singleton
    static GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            SubmissionStore submissionStore) {

        return unitOfWorkAwareProxyFactory.create(
                GradingResponseProcessor.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        SubmissionStore.class,
                        SubmissionConsumer.class},
                new Object[] {
                        mapper,
                        submissionStore,
                        new NoOpSubmissionConsumer()});
    }
}
