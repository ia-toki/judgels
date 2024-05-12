package judgels.fs.local;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.fs.NaturalFilenameComparator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public final class LocalFileSystem implements FileSystem {
    private static final Set<String> IGNORABLE_FILES = ImmutableSet.of(
            ".gitkeep",
            "__MACOSX"
    );

    private static final Set<PosixFilePermission> PERMISSION_700 = PosixFilePermissions.fromString("rwx------");
    private static final Set<PosixFilePermission> PERMISSION_600 = PosixFilePermissions.fromString("rw-------");

    private final Path baseDir;

    public LocalFileSystem(Path baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void createDirectory(Path dirPath) {
        try {
            Files.createDirectories(
                    dirPath == null ? baseDir : baseDir.resolve(dirPath),
                    PosixFilePermissions.asFileAttribute(PERMISSION_700));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean directoryExists(Path dirPath) {
        return Files.isDirectory(baseDir.resolve(dirPath));
    }

    @Override
    public void copyDirectory(Path srcPath, Path destPath) {
        try (Stream<Path> stream = Files.walk(baseDir.resolve(srcPath))) {
            stream.forEach(src -> copy(src, baseDir.resolve(destPath).resolve(baseDir.resolve(srcPath).relativize(src))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFile(Path filePath) {
        writeByteArrayToFile(filePath, new byte[0]);
    }

    @Override
    public void removeFile(Path filePath) {
        try {
            FileUtils.deleteDirectory(baseDir.resolve(filePath).toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile(Path filePath) {
        return baseDir.resolve(filePath).toFile();
    }

    @Override
    public void uploadPublicFile(Path filePath, InputStream content) {
        try {
            createDirectory(filePath.getParent());
            Files.copy(content, baseDir.resolve(filePath), StandardCopyOption.REPLACE_EXISTING);
            Files.setPosixFilePermissions(baseDir.resolve(filePath), PERMISSION_600);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPublicFileUrl(Path filePath) {
        return baseDir.resolve(filePath).toString();
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
    public void uploadZippedFiles(Path dirPath, InputStream content) {
        try {
            File destDir = baseDir.resolve(dirPath).toFile();
            byte[] buffer = new byte[4096];
            int entries = 0;
            long total = 0;

            try (ZipInputStream zis = new ZipInputStream(content)) {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String fileFullName = ze.getName();
                    boolean isDirectoryEntry = ze.isDirectory();
                    boolean isFileEntryInIgnorableDirectory = IGNORABLE_FILES.stream()
                            .anyMatch(dir -> fileFullName.contains(dir + "/"));

                    if (isDirectoryEntry || isFileEntryInIgnorableDirectory) {
                        zis.closeEntry();
                        ze = zis.getNextEntry();
                        continue;
                    }

                    String filename = FilenameUtils.getName(fileFullName);
                    File file = new File(destDir, filename);
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                            total += len;
                        }
                        entries++;
                    }

                    zis.closeEntry();
                    if (entries > 4096) {
                        throw new IllegalArgumentException("Too many files to unzip.");
                    }
                    if (total > 0x40000000) { // 1GB
                        throw new IllegalArgumentException("File too big to unzip.");
                    }

                    ze = zis.getNextEntry();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(Path dirPath) {
        File[] files = baseDir.resolve(dirPath).toFile().listFiles();
        if (files == null) {
            return ImmutableList.of();
        }

        List<FileInfo> fileInfos = Lists.newArrayList(Arrays.stream(files)
                .filter(File::isDirectory)
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
        File[] files = baseDir.resolve(dirPath).toFile().listFiles();
        if (files == null) {
            return ImmutableList.of();
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
    public void writeByteArrayToFile(Path filePath, byte[] content) {
        InputStream stream = new ByteArrayInputStream(content);
        uploadPublicFile(filePath, stream);
    }

    @Override
    public byte[] readByteArrayFromFile(Path filePath) {
        try (InputStream stream = new FileInputStream(baseDir.resolve(filePath).toFile())) {
            return ByteStreams.toByteArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copy(Path src, Path dest) {
        try {
            Files.copy(src, dest, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
