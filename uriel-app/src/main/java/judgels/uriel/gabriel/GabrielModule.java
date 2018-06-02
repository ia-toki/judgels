package judgels.uriel.gabriel;

import dagger.Module;
import dagger.Provides;

@Module
public class GabrielModule {
    private final GabrielConfiguration config;

    public GabrielModule(GabrielConfiguration config) {
        this.config = config;
    }

    @Provides
    @GabrielClientJid
    String gabrielClientJid() {
        return config.getClientJid();
    }
}
