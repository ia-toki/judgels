package judgels.jophiel.user.avatar;

import dagger.Module;
import dagger.Provides;
import java.io.File;
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
        public void createDirectory(Path dirPath) {}

        @Override
        public boolean directoryExists(Path dirPath) {
            return false;
        }

        @Override
        public void createFile(Path filePath) {}

        @Override
        public void removeFile(Path filePath) {}

        @Override
        public File getFile(Path filePath) {
            return filePath.toFile();
        }

        @Override
        public void uploadPublicFile(Path filePath, InputStream content) {}

        @Override
        public String getPublicFileUrl(Path filePath) {
            return "/fake/" + filePath.toString();
        }

        @Override
        public void uploadPrivateFile(Path filePath, InputStream content) {}

        @Override
        public String getPrivateFileUrl(Path filePath) {
            return "/fake/" + filePath.toString();
        }

        @Override
        public void uploadZippedFiles(Path dirPath, File zippedFiles, boolean includeDirectory) {}

        @Override
        public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
            return Collections.emptyList();
        }

        @Override
        public List<FileInfo> listFilesInDirectory(Path dirPath) {
            return Collections.emptyList();
        }

        @Override
        public void writeByteArrayToFile(Path filePath, byte[] content) {}

        @Override
        public byte[] readByteArrayFromFile(Path filePath) {
            return new byte[0];
        }
    }
}
