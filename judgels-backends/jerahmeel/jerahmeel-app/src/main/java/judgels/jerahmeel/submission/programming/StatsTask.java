package judgels.jerahmeel.submission.programming;

import static java.util.Optional.empty;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
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
        Page<Submission> submissions = submissionStore.getSubmissionsForStats(empty(), 100);

        for (Submission s : submissions.getPage()) {
            output.write(s.getJid() + "\n");
            statsProcessor.accept(s);
        }
    }
}
