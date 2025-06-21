package judgels.uriel.file;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import judgels.contrib.fs.aws.AwsConfiguration;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.service.JudgelsBaseDataDir;

@Module
public class FileModule {
    private final FileConfiguration config;

    public FileModule(FileConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @FileFs
    FileSystem fileFs(Optional<AwsConfiguration> awsConfig, @JudgelsBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("files"));
    }
}
