package tlx.jerahmeel.tasks;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.jerahmeel.submission.JerahmeelSubmissionStore;
import judgels.jerahmeel.submission.programming.SubmissionFs;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionSourceBuilder;
import judgels.sandalphon.submission.programming.SubmissionStore;
import tlx.fs.duplex.DuplexFileSystem;

/**
 * Uploads submission files from local to AWS S3.
 */
public class UploadDuplexSubmissionsToAwsTask extends Task {
    private final FileSystem submissionFs;
    private final SubmissionStore submissionStore;
    private final SubmissionSourceBuilder submissionSourceBuilder;

    public UploadDuplexSubmissionsToAwsTask(
            @SubmissionFs FileSystem submissionFs,
            @JerahmeelSubmissionStore SubmissionStore submissionStore) {

        super("jerahmeel-upload-duplex-submissions-to-aws");

        this.submissionFs = submissionFs;
        this.submissionStore = submissionStore;
        this.submissionSourceBuilder = new SubmissionSourceBuilder(submissionFs);
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
        if (!(submissionFs instanceof DuplexFileSystem)) {
            return;
        }

        List<String> lastSubmissionIds = parameters.get("lastSubmissionId");
        Optional<Long> lastSubmissionId = lastSubmissionIds == null || lastSubmissionIds.isEmpty()
                ? empty()
                : of(Long.parseLong(lastSubmissionIds.get(0)));

        List<String> limits = parameters.get("limit");
        Optional<Integer> limit = limits == null || limits.isEmpty() ? empty() : of(Integer.parseInt(limits.get(0)));

        Page<Submission> submissions =
                submissionStore.getSubmissionsForStats(empty(), lastSubmissionId, limit.orElse(1000));

        Submission lastSubmission = null;
        for (Submission s : submissions.getPage()) {
            var source = submissionSourceBuilder.fromPastSubmission(s.getJid(), false);

            // Store to AWS
            submissionSourceBuilder.storeSubmissionSource(s.getJid(), source);

            // Remove from local
            output.write(s.getJid() + "\n");

            lastSubmission = s;
        }

        if (lastSubmission != null) {
            output.write(lastSubmission.getId() + "\n");
        }
    }
}
