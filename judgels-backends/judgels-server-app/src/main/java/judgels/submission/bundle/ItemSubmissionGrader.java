package judgels.submission.bundle;

import judgels.api.problem.bundle.Item;
import judgels.api.submission.bundle.Grading;

public interface ItemSubmissionGrader {
    Grading grade(Item item, String answer);
}
