package judgels.sandalphon.submission.bundle;

import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.ItemSubmissionGrading;

public interface ItemSubmissionGrader {
    ItemSubmissionGrading grade(Item item, String answer);
}
