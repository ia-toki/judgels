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
    public void uploadZippedFiles(Path dirPath, File zippedFiles, boolean includeDirectory) {
        local.uploadZippedFiles(dirPath, zippedFiles, includeDirectory);
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        try {
            return local.listDirectoriesInDirectory(dirPath);
        } catch (RuntimeException e) {
            return aws.listDirectoriesInDirectory(dirPath);
        }
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        try {
            return local.listFilesInDirectory(dirPath);
        } catch (RuntimeException e) {
            return aws.listFilesInDirectory(dirPath);
        }
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
