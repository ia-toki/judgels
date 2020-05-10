package org.iatoki.judgels;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class LocalFileSystemProvider implements FileSystemProvider {

    private static final Set<String> IGNORABLE_FILES = ImmutableSet.of(
            ".gitkeep",
            "__MACOSX"
    );

    private static final int TOOBIG = 0x40000000; // max size of unzipped data, 1GB
    private static final int TOOMANY = 4096;     // max number of files

    private File baseDir;

    public LocalFileSystemProvider(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void createDirectory(List<String> directoryPath) throws IOException {
        File directory = FileUtils.getFile(baseDir, toArray(directoryPath));
        FileUtils.forceMkdir(directory);
    }

    @Override
    public void createFile(List<String> filePath) throws IOException {
        writeToFile(filePath, "");
    }

    @Override
    public void removeFile(List<String> filePath) throws IOException {
        File file = FileUtils.getFile(baseDir, toArray(filePath));
        FileUtils.forceDelete(file);
    }

    @Override
    public boolean directoryExists(List<String> directoryPath) {
        return (FileUtils.getFile(baseDir, toArray(directoryPath)).exists()) && (FileUtils.getFile(baseDir, toArray(directoryPath)).isDirectory());
    }

    @Override
    public boolean fileExists(List<String> filePath) {
        return (FileUtils.getFile(baseDir, toArray(filePath)).exists()) && (FileUtils.getFile(baseDir, toArray(filePath)).isFile());
    }

    @Override
    public boolean makeFilePublic(List<String> filePath) {
        return FileUtils.getFile(baseDir, toArray(filePath)).setReadable(true);
    }

    @Override
    public boolean makeFilePrivate(List<String> filePath) {
        return FileUtils.getFile(baseDir, toArray(filePath)).setReadable(false, true);
    }

    @Override
    public void writeToFile(List<String> filePath, String content) throws IOException {
        File file = FileUtils.getFile(baseDir, toArray(filePath));
        FileUtils.writeStringToFile(file, content);
    }

    @Override
    public void writeByteArrayToFile(List<String> filePath, byte[] content) throws IOException {
        File file = FileUtils.getFile(baseDir, toArray(filePath));
        FileUtils.writeByteArrayToFile(file, content);
    }

    @Override
    public String readFromFile(List<String> filePath) throws IOException {
        File file = FileUtils.getFile(baseDir, toArray(filePath));
        return FileUtils.readFileToString(file);
    }

    @Override
    public byte[] readByteArrayFromFile(List<String> filePath) throws IOException {
        File file = FileUtils.getFile(baseDir, toArray(filePath));
        return FileUtils.readFileToByteArray(file);
    }

    @Override
    public void uploadFile(List<String> destinationDirectoryPath, File file, String destinationFilename) throws IOException {
        uploadFileFromStream(destinationDirectoryPath, new FileInputStream(file), destinationFilename);
    }

    @Override
    public void uploadFileFromStream(List<String> destinationDirectoryPath, InputStream inputStream, String destinationFilename) throws IOException {
        File destinationFile = FileUtils.getFile(FileUtils.getFile(baseDir, toArray(destinationDirectoryPath)), destinationFilename);
        FileUtils.copyInputStreamToFile(inputStream, destinationFile);
    }

    @Override
    public void uploadZippedFiles(List<String> destinationDirectoryPath, File zippedFiles, boolean includeDirectory) throws IOException {
        File destinationDirectory = FileUtils.getFile(baseDir, toArray(destinationDirectoryPath));
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zippedFiles));
        byte[] buffer = new byte[4096];
        int entries = 0;
        long total = 0;
        ZipEntry ze = zis.getNextEntry();
        try {
            while (ze != null) {
                String filename = ze.getName();
                File file = new File(destinationDirectory, filename);
                if ((includeDirectory) && (ze.isDirectory())) {
                    file.mkdirs();
                } else {
                    if (((includeDirectory) && (file.getCanonicalPath().startsWith(destinationDirectory.getCanonicalPath()))) || (destinationDirectory.getAbsolutePath().equals(file.getParentFile().getAbsolutePath()))) {
                        FileOutputStream fos = new FileOutputStream(file);
                        try {
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                                total += len;
                            }
                            entries++;
                        } finally {
                            fos.close();
                        }
                    }
                }

                zis.closeEntry();
                if (entries > TOOMANY) {
                    throw new IllegalStateException("Too many files to unzip.");
                }
                if (total > TOOBIG) {
                    throw new IllegalStateException("File too big to unzip.");
                }

                ze = zis.getNextEntry();
            }
        } finally {
            zis.close();
        }
    }

    @Override
    public ByteArrayOutputStream getZippedFilesInDirectory(List<String> directoryPath) throws IOException {
        File rootDirectory = FileUtils.getFile(baseDir, toArray(directoryPath));
        List<File> files = getAllFilesRecursively(rootDirectory);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        ZipOutputStream zos = new ZipOutputStream(os);
        try {
            for (File file : files) {
                int beginIndex = file.getAbsolutePath().indexOf(rootDirectory.getAbsolutePath()) + rootDirectory.getAbsolutePath().length() + 1;
                ZipEntry ze = new ZipEntry(file.getAbsolutePath().substring(beginIndex).replace("\\", "/"));
                zos.putNextEntry(ze);

                try (FileInputStream fin = new FileInputStream(file)) {
                    int len;
                    while ((len = fin.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
            }

            zos.closeEntry();
        } finally {
            zos.close();
        }

        return os;
    }

    @Override
    public List<FileInfo> listFilesInDirectory(List<String> directoryPath) {
        File directory = FileUtils.getFile(baseDir, toArray(directoryPath));
        File[] files = directory.listFiles();

        if (files == null) {
            return ImmutableList.of();
        }

        List<File> filteredFiles = Arrays.asList(files).stream().filter(f -> f.isFile()).collect(Collectors.toList());

        ArrayList<FileInfo> fileInfos = Lists.newArrayList(Lists.transform(filteredFiles, file -> new FileInfo(file.getName(), file.length(), new Date(file.lastModified()))));

        Comparator<String> comparator = new NaturalFilenameComparator();
        Collections.sort(fileInfos, (FileInfo a, FileInfo b) -> comparator.compare(a.getName(), b.getName()));

        return fileInfos.stream().filter(f -> !IGNORABLE_FILES.contains(f.getName())).collect(Collectors.toList());
    }

    @Override
    public List<FileInfo> listDirectoriesInDirectory(List<String> directoryPath) {
        File directory = FileUtils.getFile(baseDir, toArray(directoryPath));
        File[] files = directory.listFiles();

        if (files == null) {
            return ImmutableList.of();
        }

        List<File> filteredFiles = Arrays.asList(files).stream().filter(f -> f.isDirectory()).collect(Collectors.toList());

        ArrayList<FileInfo> fileInfos = Lists.newArrayList(Lists.transform(filteredFiles, file -> new FileInfo(file.getName(), file.length(), new Date(file.lastModified()))));

        Comparator<String> comparator = new NaturalFilenameComparator();
        Collections.sort(fileInfos, (FileInfo a, FileInfo b) -> comparator.compare(a.getName(), b.getName()));

        return ImmutableList.copyOf(fileInfos);
    }

    @Override
    public String getURL(List<String> filePath) {
        return FileUtils.getFile(baseDir, toArray(filePath)).getAbsolutePath();
    }

    private List<File> getAllFilesRecursively(File rootDirectory) {
        ImmutableList.Builder<File> files = ImmutableList.builder();
        visitDirectory(rootDirectory, files);
        return files.build();
    }

    private void visitDirectory(File node, ImmutableList.Builder<File> files) {
        if (node.isFile()) {
            files.add(node);
        } else {
            File[] newNodes = node.listFiles();
            if (newNodes != null) {
                for (File newNode : newNodes) {
                    visitDirectory(newNode, files);
                }
            }
        }
    }

    private String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }
}
