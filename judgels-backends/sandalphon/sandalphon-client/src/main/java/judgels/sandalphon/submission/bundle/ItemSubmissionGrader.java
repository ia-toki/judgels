package judgels.sandalphon.submission.bundle;

import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.Grading;

public interface ItemSubmissionGrader {
    Grading grade(Item item, String answer);
}
