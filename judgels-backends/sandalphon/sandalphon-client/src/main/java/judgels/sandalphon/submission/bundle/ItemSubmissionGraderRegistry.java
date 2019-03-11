package judgels.sandalphon.submission.bundle;

import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Verdict;

public class ItemSubmissionGraderRegistry {
    private static final MultipleChoiceItemSubmissionGrader MULTIPLE_CHOICE_ITEM_SUBMISSION_GRADER =
            new MultipleChoiceItemSubmissionGrader();
    private static final ShortAnswerItemSubmissionGrader SHORT_ANSWER_ITEM_SUBMISSION_GRADER =
            new ShortAnswerItemSubmissionGrader();
    private static final FixedVerdictItemSubmissionGrader MANUAL_ITEM_SUBMISSION_GRADER =
            new FixedVerdictItemSubmissionGrader(Verdict.PENDING_MANUAL_GRADING);

    @Inject
    public ItemSubmissionGraderRegistry() {}

    public ItemSubmissionGrader get(ItemType itemType) {
        if (itemType == ItemType.MULTIPLE_CHOICE) {
            return MULTIPLE_CHOICE_ITEM_SUBMISSION_GRADER;
        } else if (itemType == ItemType.SHORT_ANSWER) {
            return SHORT_ANSWER_ITEM_SUBMISSION_GRADER;
        } else if (itemType == ItemType.ESSAY) {
            return MANUAL_ITEM_SUBMISSION_GRADER;
        }
        throw new IllegalArgumentException();
    }
}
