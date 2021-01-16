package org.iatoki.judgels.sandalphon.problem.programming.submission;

import akka.actor.Scheduler;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionRegradeProcessor;
import judgels.sandalphon.submission.programming.SubmissionStore;
import scala.concurrent.ExecutionContext;

public class SubmissionRegrader {
    private static final Duration SCHEDULE_DELAY = Duration.ofMillis(10);

    private final SubmissionStore submissionStore;
    private final Scheduler scheduler;
    private final ExecutionContext executor;
    private final SubmissionRegradeProcessor processor;

    public SubmissionRegrader(
            SubmissionStore submissionStore,
            Scheduler scheduler,
            ExecutionContext executor,
            SubmissionRegradeProcessor processor) {

        this.submissionStore = submissionStore;
        this.scheduler = scheduler;
        this.executor = executor;
        this.processor = processor;
    }

    public void regradeSubmission(Submission submission) {
        String gradingJid = submissionStore.createGrading(submission);
        processor.process(ImmutableMap.of(gradingJid, submission));
    }

    public void regradeSubmissions(List<Submission> submissions) {
        Map<String, Submission> submissionsMap = new LinkedHashMap<>();
        for (Submission submission : submissions) {
            String gradingJid = submissionStore.createGrading(submission);
            submissionsMap.put(gradingJid, submission);
        }

        scheduler.scheduleOnce(SCHEDULE_DELAY, () -> processor.process(submissionsMap), executor);
    }
}
