package judgels.uriel.submission;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.aws.AwsConfiguration;
import judgels.fs.aws.AwsFileSystem;
import judgels.fs.aws.AwsFsConfiguration;
import judgels.fs.local.LocalFileSystem;

@Module
public class SubmissionModule {
    private final SubmissionConfiguration config;

    public SubmissionModule(SubmissionConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @SubmissionFs
    FileSystem submissionFs(Optional<AwsConfiguration> awsConfig) {
        if (config.getFs() instanceof AwsFsConfiguration) {
            return new AwsFileSystem(awsConfig.get(), (AwsFsConfiguration) config.getFs());
        } else {
            return new LocalFileSystem();
        }
    }
}
