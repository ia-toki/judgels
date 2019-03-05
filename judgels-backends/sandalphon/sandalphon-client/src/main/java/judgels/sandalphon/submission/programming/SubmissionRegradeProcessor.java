package judgels.sandalphon.submission.programming;

import java.util.Map;
import javax.inject.Inject;
import judgels.gabriel.api.SubmissionSource;
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

    public void process(Map<String, Submission> submissionsMap) {
        submissionsMap.forEach((gradingJid, submission) -> {
            SubmissionSource source = submissionSourceBuilder.fromPastSubmission(submission.getJid());
            submissionClient.requestGrading(gradingJid, submission, source);
        });
    }
}
