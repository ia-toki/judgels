package judgels.gabriel.isolate;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import judgels.gabriel.sandboxes.isolate.IsolateSandboxFactory;

@Module
public class IsolateModule {
    private final Optional<IsolateConfiguration> config;

    public IsolateModule(Optional<IsolateConfiguration> config) {
        this.config = config;
    }

    @Provides
    Optional<IsolateSandboxFactory> sandboxFactory() {
        return config.map(config -> new IsolateSandboxFactory(config.getIsolatePath(), config.getIwrapperPath()));
    }
}
