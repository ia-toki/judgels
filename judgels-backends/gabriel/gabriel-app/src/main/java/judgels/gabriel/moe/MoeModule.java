package judgels.gabriel.moe;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.gabriel.sandboxes.moe.MoeSandboxFactory;

@Module
public class MoeModule {
    private final Optional<MoeConfiguration> moeConfig;

    public MoeModule(Optional<MoeConfiguration> config) {
        this.moeConfig = config;
    }

    @Provides
    Optional<MoeSandboxFactory> sandboxFactory() {
        return moeConfig.map(config -> new MoeSandboxFactory(config.getIsolatePath(), config.getIwrapperPath()));
    }
}
