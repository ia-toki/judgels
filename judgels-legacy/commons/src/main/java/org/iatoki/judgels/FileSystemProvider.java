package org.iatoki.judgels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public interface FileSystemProvider {

    void createDirectory(List<String> directoryPath) throws IOException;

    void createFile(List<String> filePath) throws IOException;

    void removeFile(List<String> filePath) throws IOException;

    boolean directoryExists(List<String> directoryPath);

    boolean fileExists(List<String> filePath);

    boolean makeFilePublic(List<String> filePath);

    boolean makeFilePrivate(List<String> filePath);

    void writeToFile(List<String> filePath, String content) throws IOException;

    void writeByteArrayToFile(List<String> filePath, byte[] content) throws IOException;

    String readFromFile(List<String> filePath) throws IOException;

    byte[] readByteArrayFromFile(List<String> filePath) throws IOException;

    void uploadFile(List<String> destinationDirectoryPath, File file, String destinationFilename) throws IOException;

    void uploadFileFromStream(List<String> destinationDirectoryPath, InputStream inputStream, String destinationFilename) throws IOException;

    void uploadZippedFiles(List<String> destinationDirectoryPath, File zippedFiles, boolean includeDirectory) throws IOException;

    ByteArrayOutputStream getZippedFilesInDirectory(List<String> directoryPath) throws IOException;

    List<FileInfo> listFilesInDirectory(List<String> directoryPath);

    List<FileInfo> listDirectoriesInDirectory(List<String> directoryPath);

    String getURL(List<String> filePath);
}
