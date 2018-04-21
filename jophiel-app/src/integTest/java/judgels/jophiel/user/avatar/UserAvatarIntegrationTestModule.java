package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import java.io.InputStream;
import java.util.List;
import javax.inject.Singleton;
import judgels.fs.FileSystem;

@Module
public class UserAvatarIntegrationTestModule {
    private UserAvatarIntegrationTestModule() {}

    @Provides
    @Singleton
    @UserAvatarFs
    static FileSystem userAvatarFs() {
        return new FakeFs();
    }

    static class FakeFs implements FileSystem {
        @Override
        public void uploadPublicFile(InputStream file, List<String> destDirPath, String destFilename) {}

        @Override
        public String getPublicFileUrl(List<String> filePath) {
            return "/fake/" + filePath.get(0);
        }
    }
}
