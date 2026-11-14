package judgels.submission.programming;

import jakarta.inject.Inject;
import java.util.Map;
import judgels.api.catalog.problem.programming.ProblemSubmissionConfig;
import judgels.api.submission.programming.Submission;
import judgels.grading.api.SubmissionSource;

public class SubmissionRegradeProcessor {
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final Submitter submitter;

    @Inject
    public SubmissionRegradeProcessor(
            SubmissionSourceBuilder submissionSourceBuilder,
            Submitter submitter) {

        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submitter = submitter;
    }

    public void process(Map<String, Submission> submissionsMap, Map<String, ProblemSubmissionConfig> configsMap) {
        submissionsMap.forEach((gradingJid, submission) -> {
            SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
            ProblemSubmissionConfig config = configsMap.get(submission.getProblemJid());
            submitter.regrade(gradingJid, submission, source, config);
        });
    }
}
