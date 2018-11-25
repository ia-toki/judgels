package judgels.uriel.submission;

import javax.inject.Inject;
import judgels.fs.FileSystem;
import judgels.sandalphon.submission.AbstractSubmissionSourceBuilder;

public class SubmissionSourceBuilder extends AbstractSubmissionSourceBuilder {
    @Inject
    public SubmissionSourceBuilder(@SubmissionFs FileSystem submissionFs) {
        super(submissionFs);
    }
}
