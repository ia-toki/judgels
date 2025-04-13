package judgels.service.gabriel;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import judgels.gabriel.api.GabrielClientConfiguration;

@Module
public final class GabrielClientModule {
    private final GabrielClientConfiguration config;

    public GabrielClientModule(GabrielClientConfiguration config) {
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
