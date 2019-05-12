package judgels.fs.local;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.MoreFiles;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    public void uploadPublicFile(Path filePath, InputStream content) {
        try {
            writeByteArrayToFile(filePath, ByteStreams.toByteArray(content));
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
    public void uploadZippedFiles(Path dirPath, File zippedFiles, boolean includeDirectory) {
        try {
            File destDir = baseDir.resolve(dirPath).toFile();
            byte[] buffer = new byte[4096];
            int entries = 0;
            long total = 0;
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFiles))) {
                ZipEntry ze = zis.getNextEntry();
                while (ze != null) {
                    String filename = ze.getName();
                    File file = new File(destDir, filename);
                    if (includeDirectory && ze.isDirectory()) {
                        file.mkdirs();
                    } else if ((includeDirectory && file.getCanonicalPath().startsWith(destDir.getCanonicalPath()))
                            || destDir.getAbsolutePath().equals(file.getParentFile().getAbsolutePath())) {
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                                total += len;
                            }
                            entries++;
                        }
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
        File[] files  = baseDir.resolve(dirPath).toFile().listFiles();
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
        File[] files  = baseDir.resolve(dirPath).toFile().listFiles();
        if (files == null) {
            return ImmutableList.of();
        }

        List<FileInfo> fileInfos = Lists.newArrayList(Arrays.stream(files)
                .filter(File::isFile)
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
        try {
            MoreFiles.createParentDirectories(baseDir.resolve(filePath));
            Files.copy(stream, baseDir.resolve(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
