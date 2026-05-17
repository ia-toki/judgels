package judgels.service.gabriel;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import judgels.grading.JudgelsServerGradingConfiguration;

@Module
public final class GabrielClientModule {
    private final JudgelsServerGradingConfiguration config;

    public GabrielClientModule(JudgelsServerGradingConfiguration config) {
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
