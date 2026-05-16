package judgels.submission.programming;

import jakarta.inject.Inject;
import java.util.Map;
import judgels.api.problem.programming.ProblemSubmissionConfig;
import judgels.api.submission.programming.Submission;
import judgels.gabriel.api.SubmissionSource;

public class SubmissionRegradeProcessor {
    private final SubmissionSourceBuilder submissionSourceBuilder;
    private final SubmissionClient submissionClient;

    @Inject
    public SubmissionRegradeProcessor(
            SubmissionSourceBuilder submissionSourceBuilder,
            SubmissionClient submissionClient) {

        this.submissionSourceBuilder = submissionSourceBuilder;
        this.submissionClient = submissionClient;
    }

    public void process(Map<String, Submission> submissionsMap, Map<String, ProblemSubmissionConfig> configsMap) {
        submissionsMap.forEach((gradingJid, submission) -> {
            SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
            ProblemSubmissionConfig config = configsMap.get(submission.getProblemJid());
            submissionClient.regrade(gradingJid, submission, source, config);
        });
    }
}
