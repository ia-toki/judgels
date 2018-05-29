package judgels.uriel.submission;

import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.aws.AwsConfiguration;
import judgels.fs.aws.AwsFileSystem;
import judgels.fs.aws.AwsFsConfiguration;
import judgels.fs.local.LocalFileSystem;

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
        if (config.getFs() instanceof AwsFsConfiguration) {
            return new AwsFileSystem(awsConfig.get(), (AwsFsConfiguration) config.getFs());
        } else {
            Path baseDir = baseDataDir.resolve("submissions");
            try {
                Files.createDirectories(baseDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new LocalFileSystem(baseDir);
        }
    }
}
