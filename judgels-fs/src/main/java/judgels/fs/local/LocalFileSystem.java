package judgels.fs.local;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;

public final class LocalFileSystem implements FileSystem {
    @Override
    public void uploadPublicFile(InputStream file, Path destDirPath, String destFilename) {
        throw new IllegalStateException();
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        throw new IllegalStateException();
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        throw new IllegalStateException();
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        throw new IllegalStateException();
    }

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        throw new IllegalStateException();
    }
}
