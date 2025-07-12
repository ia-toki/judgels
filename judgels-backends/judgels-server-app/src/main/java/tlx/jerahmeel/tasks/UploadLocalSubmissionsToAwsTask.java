package tlx.jerahmeel.tasks;

import static java.util.Optional.empty;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.jerahmeel.submission.programming.SubmissionFs;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsBaseDataDir;

/**
 * Uploads submission files from local to AWS S3.
 */
public class UploadLocalSubmissionsToAwsTask extends Task {
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder localSubmissionSourceBuilder;
    private final SubmissionSourceBuilder awsSubmissionSourceBuilder;

    public UploadLocalSubmissionsToAwsTask(
            @SubmissionFs FileSystem submissionFs,
            @JudgelsBaseDataDir Path baseDataDir,
            @JerahmeelSubmissionStore SubmissionStore submissionStore) {

        super("jerahmeel-upload-local-submissions-to-aws");

        this.submissionStore = submissionStore;
        this.localSubmissionSourceBuilder = new SubmissionSourceBuilder(new LocalFileSystem(baseDataDir.resolve("submissions")));
        this.awsSubmissionSourceBuilder = new SubmissionSourceBuilder(submissionFs);
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
        long minSubmissionId = 3881405;
        long maxSubmissionId = 3883450;

        Page<Submission> submissions =
                submissionStore.getSubmissionsForStats(empty(), Optional.of(minSubmissionId - 1), (int) (maxSubmissionId - minSubmissionId + 1));

        Submission lastSubmission = null;
        for (Submission s : submissions.getPage()) {
            var source = localSubmissionSourceBuilder.fromPastSubmission(s.getJid(), false);

            // Store to AWS
            awsSubmissionSourceBuilder.storeSubmissionSource(s.getJid(), source);

            // Remove from local
            output.write(s.getJid() + "\n");

            lastSubmission = s;
        }

        if (lastSubmission != null) {
            output.write(lastSubmission.getId() + "\n");
        }
    }
}
