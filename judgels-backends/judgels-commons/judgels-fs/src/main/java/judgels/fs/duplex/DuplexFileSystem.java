package judgels.fs.duplex;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.fs.aws.AwsFileSystem;
import judgels.fs.local.LocalFileSystem;

public final class DuplexFileSystem implements FileSystem {
    private final LocalFileSystem local;
    private final AwsFileSystem aws;

    public DuplexFileSystem(LocalFileSystem local, AwsFileSystem aws) {
        this.local = local;
        this.aws = aws;
    }

    @Override
    public void createDirectory(Path dirPath) {
        local.createDirectory(dirPath);
    }

    @Override
    public boolean directoryExists(Path dirPath) {
        return local.directoryExists(dirPath);
    }

    @Override
    public void createFile(Path filePath) {
        local.createFile(filePath);
    }

    @Override
    public void removeFile(Path filePath) {
        local.removeFile(filePath);
    }

    @Override
    public File getFile(Path filePath) {
        return local.getFile(filePath);
    }

    @Override
    public void uploadPublicFile(Path filePath, InputStream content) {
        aws.uploadPublicFile(filePath, content);
        local.uploadPublicFile(filePath, content);
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        return aws.getPublicFileUrl(filePath);
    }

    @Override
    public void uploadPrivateFile(Path filePath, InputStream content) {
        aws.uploadPrivateFile(filePath, content);
        local.uploadPrivateFile(filePath, content);
    }

    @Override
    public String getPrivateFileUrl(Path filePath) {
        return aws.getPrivateFileUrl(filePath);
    }

    @Override
    public void uploadZippedFiles(Path dirPath, InputStream content) {
        local.uploadZippedFiles(dirPath, content);
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        List<FileInfo> result = local.listDirectoriesInDirectory(dirPath);
        if (result.isEmpty()) {
            result = aws.listDirectoriesInDirectory(dirPath);
        }
        return result;
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        List<FileInfo> result = local.listFilesInDirectory(dirPath);
        if (result.isEmpty()) {
            result = aws.listFilesInDirectory(dirPath);
        }
        return result;
    }

    @Override
    public void writeByteArrayToFile(Path filePath, byte[] content) {
        aws.writeByteArrayToFile(filePath, content);
        local.writeByteArrayToFile(filePath, content);
    }

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        try {
            return local.readByteArrayFromFile(filePath);
        } catch (RuntimeException e) {
            return aws.readByteArrayFromFile(filePath);
        }
    }
}
