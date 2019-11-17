package judgels.jerahmeel.gabriel;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Named;
import judgels.gabriel.api.GabrielClientConfiguration;

@Module
public class GabrielModule {
    private final Optional<GabrielClientConfiguration> gabrielClientConfig;

    public GabrielModule(Optional<GabrielClientConfiguration> config) {
        this.gabrielClientConfig = config;
    }

    @Provides
    GabrielClientConfiguration config() {
        return gabrielClientConfig.orElse(GabrielClientConfiguration.DEFAULT);
    }

    @Provides
    @Named("gabrielClientJid")
    String gabrielClientJid(GabrielClientConfiguration config) {
        return config.getClientJid();
    }
}
