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
import judgels.core.JudgelsBaseDataDir;
import judgels.core.JudgelsScheduler;
import judgels.core.fs.FileSystem;
import judgels.core.fs.local.LocalFileSystem;
import judgels.core.messaging.MessageListener;
import judgels.core.messaging.MessageSender;
import judgels.persistence.dao.ProgrammingGradingDao;
import judgels.persistence.dao.ProgrammingSubmissionDao;
import judgels.submission.programming.BaseSubmissionStore;
import judgels.submission.programming.GradingResponsePoller;
import judgels.submission.programming.GradingResponseProcessor;
import judgels.submission.programming.NoOpSubmissionConsumer;
import judgels.submission.programming.SubmissionConsumer;
import judgels.submission.programming.SubmissionRegradeProcessor;
import judgels.submission.programming.SubmissionRegrader;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import judgels.submission.programming.Submitter;
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
    static Submitter submitter(
            SessionFactory sessionFactory,
            SubmissionStore submissionStore,
            @Named("gradingRequestQueueName") String gradingRequestQueueName,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            MessageSender messageSender,
            ObjectMapper mapper) {

        return new Submitter(
                sessionFactory,
                submissionStore,
                gradingRequestQueueName,
                gradingResponseQueueName + "-problem",
                messageSender,
                mapper);
    }

    @Provides
    @Singleton
    static SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            SubmissionStore submissionStore,
            SubmissionSourceBuilder submissionSourceBuilder,
            Submitter submitter) {

        ExecutorService executorService = scheduler.createExecutorService("problem-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(
                submissionStore,
                executorService,
                new SubmissionRegradeProcessor(submissionSourceBuilder, submitter));
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("problem-grading-response-processor-%d", 10);
        return new GradingResponsePoller(
                messageListener,
                gradingResponseQueueName + "-problem",
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
