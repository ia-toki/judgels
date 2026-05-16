package judgels.submission.programming;

import judgels.api.submission.programming.Submission;

public class NoOpSubmissionConsumer implements SubmissionConsumer {
    @Override
    public void accept(Submission submission) {}
}
