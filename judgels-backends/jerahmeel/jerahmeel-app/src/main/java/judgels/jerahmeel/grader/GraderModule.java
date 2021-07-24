package judgels.jerahmeel.grader;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import judgels.gabriel.api.GraderConfiguration;

@Module
public final class GraderModule {
    private final GraderConfiguration config;

    public GraderModule(GraderConfiguration config) {
        this.config = config;
    }

    @Provides
    @Named("gradingRequestQueueName")
    String gradingRequestQueueName() {
        return config.getGradingRequestQueueName();
    }

    @Provides
    @Named("gradingResponseQueueName")
    String gradingResponseQueueName() {
        return config.getGradingResponseQueueName();
    }
}
