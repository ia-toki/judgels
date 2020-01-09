package judgels.jerahmeel.submission.programming;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;

public class StatsTask extends Task {
    private final SubmissionStore submissionStore;
    private final StatsProcessor statsProcessor;

    public StatsTask(SubmissionStore submissionStore, StatsProcessor statsProcessor) {
        super("stats");

        this.submissionStore = submissionStore;
        this.statsProcessor = statsProcessor;
    }

    @Override
    @UnitOfWork
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        List<String> lastSubmissionIds = parameters.get("lastSubmissionId").asList();
        Optional<Long> lastSubmissionId =
                lastSubmissionIds.isEmpty() ? empty() : of(Long.parseLong(lastSubmissionIds.get(0)));

        List<String> limits = parameters.get("limit").asList();
        Optional<Integer> limit = limits.isEmpty() ? empty() : of(Integer.parseInt(limits.get(0)));

        Page<Submission> submissions = submissionStore.getSubmissionsForStats(lastSubmissionId, limit.orElse(1000));

        Submission lastSubmission = null;
        for (Submission s : submissions.getPage()) {
            statsProcessor.accept(s);
            lastSubmission = s;
        }

        if (lastSubmission != null) {
            output.write(lastSubmission.getId() + "\n");
        }
    }
}
