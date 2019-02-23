package judgels.sandalphon.submission.bundle;

import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Verdicts;

public class ItemSubmissionGraderRegistry {
    @Inject
    public ItemSubmissionGraderRegistry() {}

    public ItemSubmissionGrader get(ItemType itemType) {
        if (itemType == ItemType.STATEMENT) {
            return new SetVerdictItemSubmissionGrader(Verdicts.GRADING_NOT_NEEDED);
        } else if (itemType == ItemType.MULTIPLE_CHOICE) {
            return new MultipleChoiceItemSubmissionGrader();
        }
        throw new IllegalArgumentException();
    }
}
