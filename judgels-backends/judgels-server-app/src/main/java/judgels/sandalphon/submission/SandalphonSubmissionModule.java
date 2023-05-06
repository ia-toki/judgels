package judgels.sandalphon.submission;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import javax.inject.Named;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.messaging.MessageClient;
import judgels.messaging.rabbitmq.RabbitMQ;
import judgels.messaging.rabbitmq.RabbitMQConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.sandalphon.persistence.ProgrammingGradingDao;
import judgels.sandalphon.persistence.ProgrammingSubmissionDao;
import judgels.sandalphon.problem.base.submission.SubmissionFs;
import judgels.sandalphon.submission.programming.BaseSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionClient;
import judgels.sandalphon.submission.programming.SubmissionRegradeProcessor;
import judgels.sandalphon.submission.programming.SubmissionRegrader;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsScheduler;

@Module
public class SandalphonSubmissionModule {
    private final SandalphonConfiguration config;

    public SandalphonSubmissionModule(SandalphonConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs() {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir(), "submissions"));
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
    SubmissionRegrader submissionRegrader(
            JudgelsScheduler scheduler,
            SubmissionStore submissionStore,
            SubmissionRegradeProcessor processor) {

        ExecutorService executorService = scheduler.createExecutorService("sandalphon-submission-regrade-processor-%d", 5);
        return new SubmissionRegrader(submissionStore, executorService, processor);
    }

    @Provides
    @Named("gradingRequestQueueName")
    String gradingRequestQueueName() {
        return config.getGabrielConfig().getGradingRequestQueueName();
    }

    @Provides
    @Named("gradingResponseQueueName")
    String gradingResponseQueueName() {
        return config.getGabrielConfig().getGradingResponseQueueName();
    }

    @Provides
    RabbitMQConfiguration rabbitMQConfig() {
        return config.getRabbitMQConfig().orElse(RabbitMQConfiguration.DEFAULT);
    }

    @Provides
    @Singleton
    static MessageClient messageClient(RabbitMQConfiguration rabbitMQConfig, ObjectMapper objectMapper) {
        return new MessageClient(new RabbitMQ(rabbitMQConfig), objectMapper);
    }
}
