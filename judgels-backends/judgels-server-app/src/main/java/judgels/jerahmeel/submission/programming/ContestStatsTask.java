package judgels.jerahmeel.submission.programming;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.jerahmeel.uriel.ContestClient;
import judgels.sandalphon.api.submission.programming.Submission;

public class ContestStatsTask extends Task {
    private final ContestClient contestClient;
    private final StatsProcessor statsProcessor;

    public ContestStatsTask(ContestClient contestClient, StatsProcessor statsProcessor) {
        super("jerahmeel-stats-contest");

        this.contestClient = contestClient;
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

        List<Submission> submissions = contestClient.getSubmissionsForStats(contestJid, lastSubmissionId, limit);

        Submission lastSubmission = null;
        for (Submission s : submissions) {
            statsProcessor.accept(s);
            lastSubmission = s;
        }

        if (lastSubmission != null) {
            output.write(lastSubmission.getId() + "\n");
        }
    }
}
