package judgels.fs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InMemoryFileSystem implements FileSystem {
    private final Map<Path, byte[]> fs = new HashMap<>();

    public void addFile(Path destDirPath, String destFilename, byte[] content) {
        fs.put(destDirPath.resolve(destFilename), content);
    }

    @Override
    public void createDirectory(Path dirPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean directoryExists(Path dirPath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void createFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile(Path filePath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void uploadPublicFile(Path filePath, InputStream content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        return filePath.toString();
    }

    @Override
    public void uploadPrivateFile(Path filePath, InputStream content) {
        uploadPublicFile(filePath, content);
    }

    @Override
    public String getPrivateFileUrl(Path filePath) {
        return getPublicFileUrl(filePath);
    }

    @Override
    public void uploadZippedFiles(Path dirPath, File zippedFiles, boolean includeDirectory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        String prefix = dirPath.toString();
        if (!prefix.isEmpty()) {
            prefix += File.separator;
        }

        Set<String> seenDirectoryNames = Sets.newHashSet();
        ImmutableList.Builder<FileInfo> fileInfos = ImmutableList.builder();
        for (Map.Entry<Path, byte[]> entry : fs.entrySet()) {
            if (!entry.getKey().toString().startsWith(prefix)) {
                continue;
            }

            String key = entry.getKey().toString().substring(prefix.length());
            if (key.endsWith(File.separator) || !key.contains(File.separator)) {
                continue;
            }
            key = key.substring(0, key.lastIndexOf(File.separator));
            if (key.contains(File.separator) || seenDirectoryNames.contains(key)) {
                continue;
            }

            seenDirectoryNames.add(key);
            fileInfos.add(new FileInfo.Builder()
                    .name(key)
                    .size(0)
                    .lastModifiedTime(Instant.ofEpochSecond(42))
                    .build());
        }
        return fileInfos.build();
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        String prefix = dirPath.toString();
        if (!prefix.isEmpty()) {
            prefix += File.separator;
        }

        ImmutableList.Builder<FileInfo> fileInfos = ImmutableList.builder();
        for (Map.Entry<Path, byte[]> entry : fs.entrySet()) {
            if (!entry.getKey().toString().startsWith(prefix)) {
                continue;
            }

            String key = entry.getKey().toString().substring(prefix.length());
            if (key.contains(File.separator)) {
                continue;
            }

            fileInfos.add(new FileInfo.Builder()
                    .name(key)
                    .size(entry.getValue().length)
                    .lastModifiedTime(Instant.ofEpochSecond(42))
                    .build());
        }
        return fileInfos.build();
    }

    @Override
    public void writeByteArrayToFile(Path filePath, byte[] content) {}

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        return fs.get(filePath);
    }
}
