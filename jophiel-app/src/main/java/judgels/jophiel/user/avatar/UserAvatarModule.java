package judgels.jophiel.user.avatar;

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
public class UserAvatarModule {
    private UserAvatarConfiguration config;

    public UserAvatarModule(UserAvatarConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @UserAvatarFs
    FileSystem userAvatarFs(Optional<AwsConfiguration> awsConfig) {
        if (config.getFs() instanceof AwsFsConfiguration) {
            return new AwsFileSystem(awsConfig.get(), (AwsFsConfiguration) config.getFs());
        } else {
            return new LocalFileSystem();
        }
    }
}
