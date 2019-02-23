package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Verdicts;

public class ItemSubmissionGraderRegistry {
    private final ObjectMapper objectMapper;

    @Inject
    public ItemSubmissionGraderRegistry(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ItemSubmissionGrader get(ItemType itemType) {
        if (itemType == ItemType.STATEMENT) {
            return new SetVerdictItemSubmissionGrader(objectMapper, Verdicts.GRADING_NOT_NEEDED);
        } else if (itemType == ItemType.MULTIPLE_CHOICE) {
            return new MultipleChoiceItemSubmissionGrader(objectMapper);
        }
        throw new IllegalArgumentException();
    }
}
