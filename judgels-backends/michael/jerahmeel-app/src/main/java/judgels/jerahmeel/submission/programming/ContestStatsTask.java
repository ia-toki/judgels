package judgels.jerahmeel.submission.programming;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.uriel.api.contest.submission.programming.ContestSubmissionService;

public class ContestStatsTask extends Task {
    private final Optional<ContestSubmissionService> contestSubmissionService;
    private final StatsProcessor statsProcessor;

    public ContestStatsTask(
            Optional<ContestSubmissionService> contestSubmissionService,
            StatsProcessor statsProcessor) {

        super("stats-contest");

        this.contestSubmissionService = contestSubmissionService;
        this.statsProcessor = statsProcessor;
    }

    @Override
    @UnitOfWork
    public void execute(Map<String, List<String>> parameters, PrintWriter output) {
        if (!contestSubmissionService.isPresent()) {
            return;
        }

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

        List<Submission> submissions =
                contestSubmissionService.get().getSubmissionsForStats(contestJid, lastSubmissionId, limit);

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
