package judgels.fs;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileSystem {
    void createDirectory(Path dirPath);
    boolean directoryExists(Path dirPath);
    void createFile(Path filePath);
    void removeFile(Path filePath);
    File getFile(Path filePath);
    void uploadPublicFile(Path filePath, InputStream content);
    String getPublicFileUrl(Path filePath);
    void uploadPrivateFile(Path filePath, InputStream content);
    String getPrivateFileUrl(Path filePath);
    void uploadZippedFiles(Path dirPath, InputStream content);
    List<FileInfo> listDirectoriesInDirectory(Path dirPath);
    List<FileInfo> listFilesInDirectory(Path dirPath);
    void writeByteArrayToFile(Path filePath, byte[] content);
    byte[] readByteArrayFromFile(Path filePath);

    default void writeToFile(Path filePath, String content) {
        writeByteArrayToFile(filePath, content.getBytes());
    }

    default String readFromFile(Path filePath) {
        return new String(readByteArrayFromFile(filePath));
    }
}
