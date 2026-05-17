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
import judgels.training.submission.programming.TrainingSubmissionStore;
import judgels.training.submission.programming.StatsProcessor;
import judgels.submission.programming.SubmissionStore;

public class RefreshProblemSetStatsTask extends Task {
    private final SubmissionStore submissionStore;
    private final StatsProcessor statsProcessor;

    public RefreshProblemSetStatsTask(
            @TrainingSubmissionStore SubmissionStore submissionStore,
            StatsProcessor statsProcessor) {

        super("jerahmeel-refresh-problem-set-stats");

        this.submissionStore = submissionStore;
        this.statsProcessor = statsProcessor;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
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
            statsProcessor.accept(s);
            lastSubmission = s;
        }

        if (lastSubmission != null) {
            output.write(lastSubmission.getId() + "\n");
        }
    }
}
