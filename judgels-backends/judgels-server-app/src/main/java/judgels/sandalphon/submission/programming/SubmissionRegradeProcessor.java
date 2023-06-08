package judgels.sandalphon.submission.programming;

import java.util.Map;
import javax.inject.Inject;
import judgels.gabriel.api.SubmissionSource;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.submission.programming.Submission;

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
