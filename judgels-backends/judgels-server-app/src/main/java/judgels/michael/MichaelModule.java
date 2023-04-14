package judgels.michael;

import dagger.Module;
import dagger.Provides;
import judgels.JudgelsAppConfiguration;

@Module
public class MichaelModule {
    private final JudgelsAppConfiguration appConfig;

    public MichaelModule(JudgelsAppConfiguration appConfig) {
        this.appConfig = appConfig;
    }

    @Provides
    JudgelsAppConfiguration appConfig() {
        return appConfig;
    }
}
