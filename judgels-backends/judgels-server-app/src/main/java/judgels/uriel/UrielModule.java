package judgels.uriel;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;

@Module
public class UrielModule {
    private final UrielConfiguration config;

    public UrielModule(UrielConfiguration config) {
        this.config = config;
    }

    @Provides
    @UrielBaseDataDir
    Path urielBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }
}
