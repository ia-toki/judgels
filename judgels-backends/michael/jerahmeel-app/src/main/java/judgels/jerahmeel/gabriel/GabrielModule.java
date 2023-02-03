package judgels.jerahmeel.gabriel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import judgels.gabriel.api.GabrielClientConfiguration;

@Module
public final class GabrielModule {
    private final GabrielClientConfiguration config;

    public GabrielModule(GabrielClientConfiguration config) {
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
