package judgels.uriel.submission;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;

@Module
public class SubmissionModule {
    private final Path baseDataDir;
    private final SubmissionConfiguration config;

    public SubmissionModule(Path baseDataDir, SubmissionConfiguration config) {
        this.baseDataDir = baseDataDir;
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("submissions"));
    }
}
