package judgels.uriel.file;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;
import judgels.uriel.UrielBaseDataDir;

@Module
public class FileModule {
    private final FileConfiguration config;

    public FileModule(FileConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @FileFs
    FileSystem fileFs(Optional<AwsConfiguration> awsConfig, @UrielBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("files"));
    }
}
