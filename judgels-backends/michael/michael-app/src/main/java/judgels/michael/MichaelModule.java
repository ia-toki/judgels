package judgels.michael;

import dagger.Module;
import dagger.Provides;

@Module
public class MichaelModule {
    private final MichaelConfiguration config;

    public MichaelModule(MichaelConfiguration config) {
        this.config = config;
    }

    @Provides
    MichaelConfiguration michaelConfig() {
        return config;
    }
}
