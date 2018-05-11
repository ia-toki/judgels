package judgels.fs;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileSystem {
    void uploadPublicFile(InputStream file, Path destDirPath, String destFilename);
    String getPublicFileUrl(Path filePath);
    List<FileInfo> listDirectoriesInDirectory(Path dirPath);
    List<FileInfo> listFilesInDirectory(Path dirPath);
    byte[] readByteArrayFromFile(Path filePath);
}
