package judgels.gabriel;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.time.Clock;
import javax.inject.Singleton;
import judgels.JudgelsBaseDataDir;
import judgels.JudgelsGraderConfiguration;

@Module
public class JudgelsGraderModule {
    private final JudgelsGraderConfiguration config;

    public JudgelsGraderModule(JudgelsGraderConfiguration config) {
        this.config = config;
    }

    @Provides
    @JudgelsBaseDataDir
    Path baseDataDir() {
        return config.getBaseDataDir();
    }

    @Provides
    @Singleton
    static Clock clock() {
        return Clock.systemUTC();
    }
}
