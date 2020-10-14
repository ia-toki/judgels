package judgels.sandalphon.submission.programming;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.programming.Grading;
import judgels.sandalphon.api.submission.programming.Submission;

public class SubmissionDownloader {
    private final SubmissionSourceBuilder sourceBuilder;

    @Inject
    public SubmissionDownloader(SubmissionSourceBuilder sourceBuilder) {
        this.sourceBuilder = sourceBuilder;
    }

    public void downloadAsZip(OutputStream output, Submission submission) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(output))) {
            SubmissionSource source = sourceBuilder.fromPastSubmission(submission.getJid());
            for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
                SourceFile file = entry.getValue();
                ZipEntry ze = new ZipEntry(submission.getId() + "/" + file.getName());
                zos.putNextEntry(ze);
                zos.write(file.getContent());
                zos.closeEntry();
            }
        }
        output.flush();
    }

    public void downloadAsZip(
            OutputStream output,
            List<Submission> submissions,
            Map<String, String> usernamesMap,
            Map<String, String> problemAliasesMap) throws IOException {

        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(output))) {
            for (Submission submission : submissions) {
                String problemAlias = problemAliasesMap.get(submission.getProblemJid());
                String username = usernamesMap.get(submission.getUserJid());
                if (problemAlias == null || username == null) {
                    continue;
                }

                int points = submission.getLatestGrading().map(Grading::getScore).orElse(0);

                SubmissionSource source = sourceBuilder.fromPastSubmission(submission.getJid());
                for (Map.Entry<String, SourceFile> entry : source.getSubmissionFiles().entrySet()) {
                    SourceFile file = entry.getValue();
                    String filename = String.format(
                            "%s/%s_%s_%d_%s",
                            problemAlias, submission.getId(), username, points, file.getName());

                    ZipEntry ze = new ZipEntry(filename);
                    zos.putNextEntry(ze);
                    zos.write(file.getContent());
                    zos.closeEntry();
                }
            }
        }
        output.flush();
    }
}
