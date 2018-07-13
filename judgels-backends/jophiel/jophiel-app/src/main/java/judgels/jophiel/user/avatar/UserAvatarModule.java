package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.util.Optional;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.FileSystems;
import judgels.fs.aws.AwsConfiguration;

@Module
public class UserAvatarModule {
    private final Path baseDataDir;
    private final UserAvatarConfiguration config;

    public UserAvatarModule(Path baseDataDir, UserAvatarConfiguration config) {
        this.baseDataDir = baseDataDir;
        this.config = config;
    }

    @Provides
    @Singleton
    @UserAvatarFs
    FileSystem userAvatarFs(Optional<AwsConfiguration> awsConfig) {
        return FileSystems.get(config.getFs(), awsConfig, baseDataDir.resolve("user-avatars"));
    }
}
