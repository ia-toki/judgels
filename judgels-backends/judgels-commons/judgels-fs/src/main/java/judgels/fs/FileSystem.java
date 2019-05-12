package judgels.fs;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileSystem {
    void uploadPublicFile(Path filePath, InputStream content);
    String getPublicFileUrl(Path filePath);
    void uploadPrivateFile(Path filePath, InputStream content);
    String getPrivateFileUrl(Path filePath);
    void uploadZippedFiles(Path dirPath, File zippedFiles, boolean includeDirectory);
    List<FileInfo> listDirectoriesInDirectory(Path dirPath);
    List<FileInfo> listFilesInDirectory(Path dirPath);
    void writeByteArrayToFile(Path filePath, byte[] content);
    byte[] readByteArrayFromFile(Path filePath);
}
