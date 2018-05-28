package judgels.jophiel.user.avatar;

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
        if (config.getFs() instanceof AwsFsConfiguration) {
            return new AwsFileSystem(awsConfig.get(), (AwsFsConfiguration) config.getFs());
        } else {
            Path baseDir = baseDataDir.resolve("user-avatars");
            try {
                Files.createDirectories(baseDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new LocalFileSystem(baseDir);
        }
    }
}
