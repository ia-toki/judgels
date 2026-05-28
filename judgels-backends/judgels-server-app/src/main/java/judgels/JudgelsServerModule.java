package judgels;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import judgels.app.JudgelsAppConfiguration;
import judgels.service.JudgelsBaseDataDir;
import tlx.user.registration.UserRegistrationConfiguration;

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

    @Provides
    Optional<UserRegistrationConfiguration> userRegistrationConfig() {
        return config.getUserRegistrationConfig();
    }
}
