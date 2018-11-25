package judgels.sandalphon.submission;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import judgels.gabriel.api.SourceFile;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.submission.Grading;
import judgels.sandalphon.api.submission.Submission;

public class AbstractSubmissionDownloader {
    private final AbstractSubmissionSourceBuilder sourceBuilder;

    public AbstractSubmissionDownloader(AbstractSubmissionSourceBuilder sourceBuilder) {
        this.sourceBuilder = sourceBuilder;
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
                            "%s/%s~%s~%d/%s",
                            submission.getId(), problemAlias, username, points, file.getName());

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
