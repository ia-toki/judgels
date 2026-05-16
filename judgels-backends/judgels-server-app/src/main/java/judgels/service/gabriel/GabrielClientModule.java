package judgels.service.gabriel;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import judgels.GradingConfiguration;

@Module
public final class GabrielClientModule {
    private final GradingConfiguration config;

    public GabrielClientModule(GradingConfiguration config) {
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
