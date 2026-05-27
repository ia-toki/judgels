package judgels.grading;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;

@Module
public final class GradingModule {
    private final JudgelsServerGradingConfiguration config;

    public GradingModule(JudgelsServerGradingConfiguration config) {
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
