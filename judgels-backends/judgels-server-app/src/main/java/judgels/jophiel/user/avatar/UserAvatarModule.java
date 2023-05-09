package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.JudgelsBaseDataDir;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;

@Module
public class UserAvatarModule {
    private final UserAvatarConfiguration config;

    public UserAvatarModule(UserAvatarConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    @UserAvatarFs
    FileSystem userAvatarFs(Optional<AwsConfiguration> awsConfig, @JudgelsBaseDataDir Path baseDataDir) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("user-avatars"));
    }
}
