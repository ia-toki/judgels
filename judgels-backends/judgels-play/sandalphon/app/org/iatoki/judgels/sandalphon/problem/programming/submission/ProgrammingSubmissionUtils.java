package org.iatoki.judgels.sandalphon.problem.programming.submission;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.nio.file.Paths;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.api.GradingLanguage;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.languages.GradingLanguageRegistry;
import org.apache.commons.io.FileUtils;
import play.mvc.Http;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProgrammingSubmissionUtils {

    private static final long MAX_SUBMISSION_FILE_LENGTH = 10 * 1024 * 1024; // 10 MB

    private ProgrammingSubmissionUtils() {
        // prevent instantiation
    }

    public static SubmissionSource createSubmissionSourceFromNewSubmission(Http.MultipartFormData<File> body) throws ProgrammingSubmissionException {
        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];
        String sourceFileFieldKeysUnparsed = body.asFormUrlEncoded().get("sourceFileFieldKeys")[0];

        if (gradingLanguage == null || sourceFileFieldKeysUnparsed == null) {
            return new SubmissionSource.Builder().build();
        }

        GradingLanguage language = GradingLanguageRegistry.getInstance().get(gradingLanguage);
        List<String> sourceFileFieldKeys = Arrays.asList(sourceFileFieldKeysUnparsed.split(","));

        List<Http.MultipartFormData.FilePart<File>> fileParts = body.getFiles();
        Map<String, String> formFilenames = fileParts.stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getFilename()));
        Map<String, File> files = fileParts.stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getFile()));

        ImmutableMap.Builder<String, byte[]> formFileContentsBuilder = ImmutableMap.builder();

        for (Map.Entry<String, File> entry : files.entrySet()) {
            String key = entry.getKey();
            File file = entry.getValue();

            if (file.length() > MAX_SUBMISSION_FILE_LENGTH) {
                throw new ProgrammingSubmissionException("Source file must not exceed 10 MB");
            }

            byte[] content;
            try {
                content = FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                throw new ProgrammingSubmissionException(e.getMessage());
            }

            formFileContentsBuilder.put(key, content);
        }

        Map<String, byte[]> formFileContents = formFileContentsBuilder.build();

        ImmutableMap.Builder<String, SourceFile> submissionFiles = ImmutableMap.builder();

        for (String fieldKey : sourceFileFieldKeys) {
            if (!formFilenames.containsKey(fieldKey)) {
                throw new ProgrammingSubmissionException("You must submit a source file for '" + fieldKey + "'");
            }

            String filename = formFilenames.get(fieldKey);
            byte[] fileContent = formFileContents.get(fieldKey);

            String verification = verifyFile(language, filename);

            if (verification != null) {
                throw new ProgrammingSubmissionException(verification);
            }

            submissionFiles.put(fieldKey, new SourceFile.Builder().name(filename).content(fileContent).build());
        }

        return new SubmissionSource.Builder().submissionFiles(submissionFiles.build()).build();
    }

    public static SubmissionSource createSubmissionSourceFromPastSubmission(FileSystem localFs, FileSystem remoteFs, String submissionJid) {
        ImmutableMap.Builder<String, SourceFile> submissionFiles = ImmutableMap.builder();

        FileSystem fileSystemProvider;

        if (localFs.directoryExists(Paths.get(submissionJid))) {
            fileSystemProvider = localFs;
        } else {
            fileSystemProvider = remoteFs;
        }

        for (FileInfo fieldKey : fileSystemProvider.listDirectoriesInDirectory(Paths.get(submissionJid))) {
            List<FileInfo> sourceFilesInDir = fileSystemProvider.listFilesInDirectory(Paths.get(submissionJid, fieldKey.getName()));

            if (sourceFilesInDir.isEmpty()) {
                throw new RuntimeException("Cannot find source files for key " + fieldKey.getName() + " for submission " + submissionJid);
            }

            FileInfo sourceFile = sourceFilesInDir.get(0);

            String name = sourceFile.getName();
            byte[] content = fileSystemProvider.readByteArrayFromFile(Paths.get(submissionJid, fieldKey.getName(), name));
            submissionFiles.put(fieldKey.getName(), new SourceFile.Builder().name(name).content(content).build());
        }

        return new SubmissionSource.Builder().submissionFiles(submissionFiles.build()).build();
    }

    public static void storeSubmissionFiles(FileSystem localFs, FileSystem remoteFs, String submissionJid, SubmissionSource submissionSource) {
        List<FileSystem> fileSystemProviders = Lists.newArrayList(localFs);
        if (remoteFs != null) {
            fileSystemProviders.add(remoteFs);
        }

        for (FileSystem fileSystemProvider : fileSystemProviders) {
            fileSystemProvider.createDirectory(Paths.get(submissionJid));

            for (Map.Entry<String, SourceFile> entry : submissionSource.getSubmissionFiles().entrySet()) {
                String fieldKey = entry.getKey();
                SourceFile sourceFile = entry.getValue();
                fileSystemProvider.writeByteArrayToFile(Paths.get(submissionJid, fieldKey, sourceFile.getName()), sourceFile.getContent());
            }
        }
    }

    private static String verifyFile(judgels.gabriel.api.GradingLanguage language, String filename) {
        Set<String> allowedExtensions = ImmutableSet.copyOf(language.getAllowedExtensions());

        int dotPos = filename.lastIndexOf('.');
        if (dotPos == -1) {
            return "Filename must have one of this extensions: " + Joiner.on(", ").join(allowedExtensions);
        }

        String extension = filename.substring(dotPos + 1);
        if (allowedExtensions.contains(extension)) {
            return null;
        } else {
            return "Filename must have one of this extensions: " + Joiner.on(", ").join(allowedExtensions);
        }
    }
}
