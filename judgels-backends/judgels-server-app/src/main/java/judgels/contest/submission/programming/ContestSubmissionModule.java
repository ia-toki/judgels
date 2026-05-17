package judgels.contest.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.contest.submission.bundle.ContestItemSubmissionStore;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.messaging.MessageListener;
import judgels.persistence.ContestBundleItemSubmissionDao;
import judgels.persistence.ContestProgrammingGradingDao;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.service.JudgelsBaseDataDir;
import judgels.service.JudgelsScheduler;
import judgels.submission.bundle.BaseItemSubmissionStore;
import judgels.submission.bundle.ItemSubmissionStore;
import judgels.submission.programming.BaseSubmissionStore;
import judgels.submission.programming.GradingResponsePoller;
import judgels.submission.programming.GradingResponseProcessor;
import judgels.submission.programming.NoOpSubmissionConsumer;
import judgels.submission.programming.SubmissionClient;
import judgels.submission.programming.SubmissionConsumer;
import judgels.submission.programming.SubmissionDownloader;
import judgels.submission.programming.SubmissionRegradeProcessor;
import judgels.submission.programming.SubmissionRegrader;
import judgels.submission.programming.SubmissionSourceBuilder;
import judgels.submission.programming.SubmissionStore;
import org.hibernate.SessionFactory;

@Module
public class ContestSubmissionModule {
    private final Optional<FileSystem> fs;

    public ContestSubmissionModule() {
        this.fs = Optional.empty();
    }

    public ContestSubmissionModule(FileSystem fs) {
        this.fs = Optional.of(fs);
    }

    @Provides
    @Singleton
    @ContestSubmissionFs
    FileSystem submissionFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("submissions")));
    }

    @Provides
    @Singleton
    @ContestSubmissionStore
    static SubmissionStore submissionStore(
            ContestProgrammingSubmissionDao submissionDao,
            ContestProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }

    @Provides
    @Singleton
    @ContestItemSubmissionStore
    static ItemSubmissionStore itemSubmissionStore(ContestBundleItemSubmissionDao submissionDao) {
        return new BaseItemSubmissionStore<>(submissionDao);
    }

    @Provides
    @Singleton
    @ContestSubmissionSourceBuilder
    static SubmissionSourceBuilder submissionSourceBuilder(@ContestSubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    @ContestSubmissionClient
    static SubmissionClient submissionClient(
            SessionFactory sessionFactory,
            @ContestSubmissionStore SubmissionStore submissionStore,
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
    static SubmissionDownloader submissionDownloader(
            @ContestSubmissionSourceBuilder SubmissionSourceBuilder submissionSourceBuilder) {
        return new SubmissionDownloader(submissionSourceBuilder);
    }

    @Provides
    @Singleton
    @ContestSubmissionRegrader
    static SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            @ContestSubmissionStore SubmissionStore submissionStore,
            @ContestSubmissionSourceBuilder SubmissionSourceBuilder submissionSourceBuilder,
            @ContestSubmissionClient SubmissionClient submissionClient) {

        ExecutorService executorService = scheduler.createExecutorService("contest-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(
                submissionStore,
                executorService,
                new SubmissionRegradeProcessor(submissionSourceBuilder, submissionClient));
    }

    @Provides
    @Singleton
    @ContestGradingResponsePoller
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            @ContestGradingResponseProcessor GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("contest-grading-response-processor-%d", 10);
        return new GradingResponsePoller(
                messageListener,
                gradingResponseQueueName,
                (ThreadPoolExecutor) executorService,
                processor);
    }

    @Provides
    @Singleton
    @ContestGradingResponseProcessor
    static GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            @ContestSubmissionStore SubmissionStore submissionStore) {

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
