package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import judgels.fs.FileInfo;
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
        public void uploadPublicFile(InputStream file, Path destDirPath, String destFilename) {}

        @Override
        public String getPublicFileUrl(Path filePath) {
            return "/fake/" + filePath.toString();
        }

        @Override
        public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
            return Collections.emptyList();
        }

        @Override
        public List<FileInfo> listFilesInDirectory(Path dirPath) {
            return Collections.emptyList();
        }

        @Override
        public byte[] readByteArrayFromFile(Path filePath) {
            return new byte[0];
        }
    }
}
