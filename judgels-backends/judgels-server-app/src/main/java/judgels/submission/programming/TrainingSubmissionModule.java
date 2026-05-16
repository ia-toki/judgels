package judgels.submission.programming;

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
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.messaging.MessageListener;
import judgels.persistence.ContestProgrammingGradingDao;
import judgels.persistence.ContestProgrammingSubmissionDao;
import judgels.persistence.TrainingProgrammingGradingDao;
import judgels.persistence.TrainingProgrammingSubmissionDao;
import judgels.service.JudgelsBaseDataDir;
import judgels.service.JudgelsScheduler;
import judgels.stats.StatsConfiguration;
import judgels.submission.JerahmeelSubmissionStore;
import judgels.submission.UrielSubmissionStore;
import org.hibernate.SessionFactory;

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
    @Singleton
    @TrainingSubmissionFs
    FileSystem submissionFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("submissions")));
    }

    @Provides
    @Singleton
    @JerahmeelSubmissionStore
    static SubmissionStore jerahmeelSubmissionStore(
            TrainingProgrammingSubmissionDao submissionDao,
            TrainingProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }

    @Provides
    @Singleton
    @UrielSubmissionStore
    static SubmissionStore urielSubmissionStore(
            ContestProgrammingSubmissionDao submissionDao,
            ContestProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }

    @Provides
    @Singleton
    static SubmissionSourceBuilder submissionSourceBuilder(@TrainingSubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }

    @Provides
    @Singleton
    static SubmissionClient submissionClient(
            SessionFactory sessionFactory,
            @JerahmeelSubmissionStore SubmissionStore submissionStore,
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
            @JerahmeelSubmissionStore SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("jerahmeel-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Singleton
    static GradingResponsePoller gradingResponsePoller(
            JudgelsScheduler scheduler,
            MessageListener messageListener,
            @Named("gradingResponseQueueName") String gradingResponseQueueName,
            GradingResponseProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("jerahmeel-grading-response-processor-%d", 10);
        return new GradingResponsePoller(
                messageListener,
                gradingResponseQueueName,
                (ThreadPoolExecutor) executorService,
                processor);
    }

    @Provides
    @Singleton
    GradingResponseProcessor gradingResponseProcessor(
            UnitOfWorkAwareProxyFactory unitOfWorkAwareProxyFactory,
            ObjectMapper mapper,
            @JerahmeelSubmissionStore SubmissionStore submissionStore,
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
