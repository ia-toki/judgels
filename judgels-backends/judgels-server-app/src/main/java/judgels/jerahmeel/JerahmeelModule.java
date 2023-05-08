package judgels.jerahmeel;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;

@Module
public class JerahmeelModule {
    private final JerahmeelConfiguration config;

    public JerahmeelModule(JerahmeelConfiguration config) {
        this.config = config;
    }

    @Provides
    @JerahmeelBaseDataDir
    Path jerahmeelBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }
}
