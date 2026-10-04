package judgels.user.avatar;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import judgels.core.JudgelsBaseDataDir;
import judgels.core.fs.FileSystem;
import judgels.core.fs.local.LocalFileSystem;

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
