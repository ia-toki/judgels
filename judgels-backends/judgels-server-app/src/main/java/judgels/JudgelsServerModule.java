package judgels;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;

@Module
public class JudgelsServerModule {
    private final JudgelsServerConfiguration config;

    public JudgelsServerModule(JudgelsServerConfiguration config) {
        this.config = config;
    }

    @Provides
    @JudgelsBaseDataDir
    Path baseDataDir() {
        return config.getBaseDataDir();
    }

    @Provides
    JudgelsAppConfiguration appConfig() {
        return config.getAppConfig();
    }
}
