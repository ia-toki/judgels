package judgels.submission.bundle;

import java.util.Map;
import java.util.Optional;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.ItemSubmission;

public class NoOpItemSubmissionConsumer implements ItemSubmissionConsumer {
    @Override
    public void accept(ItemSubmission submission, Map<String, Optional<Grading>> itemGradingsMap) {}
}
