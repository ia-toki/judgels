package tlx.training.submission.programming;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import jakarta.inject.Named;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.messaging.MessageListener;
import judgels.persistence.dao.TrainingProgrammingGradingDao;
import judgels.persistence.dao.TrainingProgrammingSubmissionDao;
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
import tlx.TlxScope;
import tlx.stats.StatsConfiguration;

@Module
public class TrainingSubmissionModule {
    private final StatsConfiguration statsConfig;
    private final Optional<FileSystem> fs;

    public TrainingSubmissionModule(StatsConfiguration statsConfig) {
        this.statsConfig = statsConfig;
        this.fs = Optional.empty();
    }

    public TrainingSubmissionModule(StatsConfiguration statsConfig, FileSystem fs) {
        this.statsConfig = statsConfig;
        this.fs = Optional.of(fs);
    }

    @Provides
    @TlxScope
    @TrainingSubmissionFs
    FileSystem submissionFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("submissions")));
    }

    @Provides
    @TlxScope
    @TrainingSubmissionStore
    static SubmissionStore trainingSubmissionStore(
            TrainingProgrammingSubmissionDao submissionDao,
            TrainingProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }

    @Provides
    @TlxScope
    @TrainingSubmissionSourceBuilder
    static SubmissionSourceBuilder submissionSourceBuilder(@TrainingSubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @TlxScope
    @TrainingSubmissionClient
    static SubmissionClient submissionClient(
            SessionFactory sessionFactory,
            @TrainingSubmissionStore SubmissionStore submissionStore,
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
    @TlxScope
    @TrainingSubmissionRegrader
    static SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            @TrainingSubmissionStore SubmissionStore submissionStore,
            @TrainingSubmissionSourceBuilder SubmissionSourceBuilder submissionSourceBuilder,
            @TrainingSubmissionClient SubmissionClient submissionClient) {

        ExecutorService executorService = scheduler.createExecutorService("training-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(
                submissionStore,
                executorService,
                new SubmissionRegradeProcessor(submissionSourceBuilder, submissionClient));
    }

    @Provides
    @TlxScope
    @TrainingGradingResponsePoller
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            @TrainingGradingResponseProcessor GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("training-grading-response-processor-%d", 10);
        return new GradingResponsePoller(
                messageListener,
                gradingResponseQueueName,
                (ThreadPoolExecutor) executorService,
                processor);
    }

    @Provides
    @TlxScope
    @TrainingGradingResponseProcessor
    GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            @TrainingSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        return unitOfWorkAwareProxyFactory.create(
                GradingResponseProcessor.class,
                new Class<?>[] {
                        ObjectMapper.class,
                        SubmissionStore.class,
                        SubmissionConsumer.class},
                new Object[] {
                        mapper,
                        submissionStore,
                        statsConfig.getEnabled() ? statsProcessor : new NoOpSubmissionConsumer()});
    }
}
