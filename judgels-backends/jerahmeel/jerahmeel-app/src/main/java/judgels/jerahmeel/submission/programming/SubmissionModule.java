package judgels.jerahmeel.submission.programming;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;
import judgels.jerahmeel.JerahmeelBaseDataDir;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;

@Module
public class SubmissionModule {
    private final SubmissionConfiguration config;

    public SubmissionModule(SubmissionConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig, @JerahmeelBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("submissions"));
    }

    @Provides
    @Singleton
    SubmissionSourceBuilder submissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        return new SubmissionSourceBuilder(submissionFs);
    }
}
