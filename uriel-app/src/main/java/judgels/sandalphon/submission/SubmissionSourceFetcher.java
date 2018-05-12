package judgels.sandalphon.submission;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import judgels.fs.FileInfo;
import judgels.fs.FileSystem;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.Submission;
import judgels.uriel.submission.SubmissionFs;

public class SubmissionSourceFetcher {
    private final FileSystem submissionFs;

    @Inject
    public SubmissionSourceFetcher(@SubmissionFs FileSystem submissionFs) {
        this.submissionFs = submissionFs;
    }

    public SubmissionSource fetchSubmissionSource(Submission submission) {
        Map<String, SourceFile> sourceFiles = new HashMap<>();

        for (FileInfo key : submissionFs.listDirectoriesInDirectory(Paths.get(submission.getJid()))) {
            List<FileInfo> files = submissionFs.listFilesInDirectory(Paths.get(submission.getJid(), key.getName()));
            if (files.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot find source files for key " + key.getName() + " for submission " + submission.getJid());
            }

            FileInfo file = files.get(0);
            String name = file.getName();
            byte[] content = submissionFs.readByteArrayFromFile(Paths.get(submission.getJid(), key.getName(), name));

            sourceFiles.put(key.getName(), new SourceFile.Builder()
                    .name(name)
                    .content(content)
                    .build());
        }

        return new SubmissionSource.Builder()
                .putAllFiles(sourceFiles)
                .build();
    }
}
