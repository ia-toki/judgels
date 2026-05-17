package judgels.grading;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;

@Module
public final class GradingClientModule {
    private final JudgelsServerGradingConfiguration config;

    public GradingClientModule(JudgelsServerGradingConfiguration config) {
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
