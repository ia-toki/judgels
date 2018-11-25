package judgels.uriel.submission;

import javax.inject.Inject;
import judgels.sandalphon.submission.AbstractSubmissionDownloader;

public class SubmissionDownloader extends AbstractSubmissionDownloader {
    @Inject
    public SubmissionDownloader(SubmissionSourceBuilder sourceBuilder) {
        super(sourceBuilder);
    }
}
