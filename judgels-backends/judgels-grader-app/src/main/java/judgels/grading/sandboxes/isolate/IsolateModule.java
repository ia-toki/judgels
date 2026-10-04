package judgels.grading.sandboxes.isolate;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;

@Module
public class IsolateModule {
    private final Optional<IsolateConfiguration> config;

    public IsolateModule(Optional<IsolateConfiguration> config) {
        this.config = config;
    }

    @Provides
    Optional<IsolateSandboxFactory> sandboxFactory() {
        return config.map(config -> new IsolateSandboxFactory(config.getBaseDir()));
    }
}
