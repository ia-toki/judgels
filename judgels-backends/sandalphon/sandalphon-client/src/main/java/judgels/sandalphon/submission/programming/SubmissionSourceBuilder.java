package judgels.sandalphon.submission.programming;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

public class SubmissionSourceBuilder {
    private static final String SOURCE_FILES_PART_PREFIX = "sourceFiles.";

    private final FileSystem submissionFs;

    public SubmissionSourceBuilder(FileSystem submissionFs) {
        this.submissionFs = submissionFs;
    }

    // TODO(fushar): unit test
    public SubmissionSource fromNewSubmission(FormDataMultiPart parts) {
        Map<String, SourceFile> submissionFiles = new HashMap<>();
        for (Map.Entry<String, List<FormDataBodyPart>> entry : parts.getFields().entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(SOURCE_FILES_PART_PREFIX)) {
                continue;
            }
            FormDataBodyPart value = entry.getValue().get(0);

            byte[] content;
            try {
                if (value.getEntity() instanceof byte[]) {
                    content = (byte[]) value.getEntity();
                } else {
                    content = ByteStreams.toByteArray(((BodyPartEntity) value.getEntity()).getInputStream());
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
            SourceFile sourceFile = new SourceFile.Builder()
                    .name(value.getContentDisposition().getFileName())
                    .content(content)
                    .build();

            submissionFiles.put(key.substring(SOURCE_FILES_PART_PREFIX.length()), sourceFile);
        }
        return new SubmissionSource.Builder().putAllSubmissionFiles(submissionFiles).build();
    }

    // TODO(fushar): unit test
    public void storeSubmissionSource(String submissionJid, SubmissionSource source) {
        for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
            Path filePath = Paths.get(submissionJid, entry.getKey(), entry.getValue().getName());
            byte[] content = entry.getValue().getContent();
            submissionFs.writeByteArrayToFile(filePath, content);
        }
    }

    public SubmissionSource fromPastSubmission(String submissionJid) {
        return fromPastSubmission(submissionJid, false);
    }

    public SubmissionSource fromPastSubmission(String submissionJid, boolean includeViewableSourcesOnly) {
        Map<String, SourceFile> sourceFiles = new HashMap<>();
        for (FileInfo key : submissionFs.listDirectoriesInDirectory(Paths.get(submissionJid))) {
            List<FileInfo> files = submissionFs.listFilesInDirectory(Paths.get(submissionJid, key.getName()));
            checkState(!files.isEmpty(), "Missing file for {} of submission {}", key.getName(), submissionJid);
            checkState(files.size() == 1, "Found multiple files for {} of submission {}", key.getName(), submissionJid);

            FileInfo file = files.get(0);
            String name = file.getName();
            byte[] content;

            if (includeViewableSourcesOnly && name.toLowerCase().endsWith(".zip")) {
                content = new byte[0];
            } else {
                content = submissionFs.readByteArrayFromFile(Paths.get(submissionJid, key.getName(), name));
            }

            sourceFiles.put(key.getName(), new SourceFile.Builder()
                    .name(name)
                    .content(content)
                    .build());
        }

        return new SubmissionSource.Builder()
                .putAllSubmissionFiles(sourceFiles)
                .build();
    }
}
