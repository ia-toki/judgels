package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.service.JudgelsBaseDataDir;

@Module
public class UserAvatarModule {
    private final Optional<FileSystem> fs;

    public UserAvatarModule() {
        this.fs = Optional.empty();
    }

    public UserAvatarModule(FileSystem fs) {
        this.fs = Optional.of(fs);
    }

    @Provides
    @Singleton
    @UserAvatarFs
    FileSystem userAvatarFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("user-avatars")));
    }
}
