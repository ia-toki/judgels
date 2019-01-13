package judgels.uriel.gabriel;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import judgels.gabriel.api.GabrielClientConfiguration;

@Module
public class GabrielModule {
    private final GabrielClientConfiguration config;

    public GabrielModule(GabrielClientConfiguration config) {
        this.config = config;
    }

    @Provides
    @Named("gabrielClientJid")
    String gabrielClientJid() {
        return config.getClientJid();
    }
}
