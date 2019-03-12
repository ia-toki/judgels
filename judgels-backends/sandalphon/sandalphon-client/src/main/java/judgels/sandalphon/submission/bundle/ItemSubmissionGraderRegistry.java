package judgels.sandalphon.submission.bundle;

import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.ItemType;

public class ItemSubmissionGraderRegistry {
    private static final MultipleChoiceItemSubmissionGrader MULTIPLE_CHOICE_ITEM_SUBMISSION_GRADER =
            new MultipleChoiceItemSubmissionGrader();

    @Inject
    public ItemSubmissionGraderRegistry() {}

    public ItemSubmissionGrader get(ItemType itemType) {
        if (itemType == ItemType.MULTIPLE_CHOICE) {
            return MULTIPLE_CHOICE_ITEM_SUBMISSION_GRADER;
        }
        throw new IllegalArgumentException();
    }
}
