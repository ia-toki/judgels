package judgels.sandalphon.submission.programming;

import judgels.sandalphon.api.submission.programming.Submission;

public class NoOpSubmissionConsumer implements SubmissionConsumer {
    @Override
    public void accept(Submission submission) {}
}
