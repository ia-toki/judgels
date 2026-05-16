package judgels.tasks;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.api.submission.programming.Submission;
import judgels.persistence.api.Page;
import judgels.submission.ContestSubmissionStore;
import judgels.submission.programming.StatsProcessor;
import judgels.submission.programming.SubmissionStore;

public class RefreshContestStatsTask extends Task {
    private final SubmissionStore submissionStore;
    private final StatsProcessor statsProcessor;

    public RefreshContestStatsTask(
            @ContestSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        super("jerahmeel-refresh-contest-stats");

        this.submissionStore = submissionStore;
        this.statsProcessor = statsProcessor;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
        List<String> contestJids = parameters.get("contestJid");
        if (contestJids == null || contestJids.isEmpty()) {
            return;
        }

        String contestJid = contestJids.get(0);

        List<String> lastSubmissionIds = parameters.get("lastSubmissionId");
        Optional<Long> lastSubmissionId = lastSubmissionIds == null || lastSubmissionIds.isEmpty()
                ? empty()
                : of(Long.parseLong(lastSubmissionIds.get(0)));

        List<String> limits = parameters.get("limit");
        Optional<Integer> limit = limits == null || limits.isEmpty() ? empty() : of(Integer.parseInt(limits.get(0)));

        Page<Submission> submissions =
                submissionStore.getSubmissionsForStats(of(contestJid), lastSubmissionId, limit.orElse(100));

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
