package judgels.fs.local;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.fs.NaturalFilenameComparator;

public final class LocalFileSystem implements FileSystem {
    private static final Set<String> IGNORABLE_FILES = ImmutableSet.of(
            ".gitkeep",
            "__MACOSX"
    );

    private final Path baseDir;

    public LocalFileSystem(Path baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void uploadPublicFile(InputStream file, Path destDirPath, String destFilename) {
        Path destFilePath = baseDir.resolve(destDirPath).resolve(destFilename);
        try {
            Files.copy(file, destFilePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        return baseDir.resolve(filePath).toString();
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        File[] files  = baseDir.resolve(dirPath).toFile().listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<FileInfo> fileInfos = Lists.newArrayList(Arrays.stream(files)
                .filter(File::isFile)
                .filter(file -> !IGNORABLE_FILES.contains(file.getName()))
                .map(file -> new FileInfo.Builder()
                        .name(file.getName())
                        .size(file.length())
                        .lastModifiedTime(Instant.ofEpochMilli(file.lastModified()))
                        .build())
                .collect(Collectors.toList()));

        Comparator<String> comparator = new NaturalFilenameComparator();
        fileInfos.sort((FileInfo f1, FileInfo f2) -> comparator.compare(f1.getName(), f2.getName()));
        return ImmutableList.copyOf(fileInfos);
    }

    @Override
    public List<FileInfo> listFilesInDirectory(Path dirPath) {
        File[] files  = baseDir.resolve(dirPath).toFile().listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<FileInfo> fileInfos = Lists.newArrayList(Arrays.stream(files)
                .filter(File::isDirectory)
                .map(file -> new FileInfo.Builder()
                        .name(file.getName())
                        .size(file.length())
                        .lastModifiedTime(Instant.ofEpochMilli(file.lastModified()))
                        .build())
                .collect(Collectors.toList()));

        Comparator<String> comparator = new NaturalFilenameComparator();
        fileInfos.sort((FileInfo f1, FileInfo f2) -> comparator.compare(f1.getName(), f2.getName()));
        return ImmutableList.copyOf(fileInfos);
    }

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        try (InputStream stream = new FileInputStream(baseDir.resolve(filePath).toFile())) {
            return ByteStreams.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
