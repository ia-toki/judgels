package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.ItemSubmissionGrading;

public interface ItemSubmissionGrader {
    ItemSubmissionGrading grade(ObjectMapper objectMapper, Item item, String answer);
}
