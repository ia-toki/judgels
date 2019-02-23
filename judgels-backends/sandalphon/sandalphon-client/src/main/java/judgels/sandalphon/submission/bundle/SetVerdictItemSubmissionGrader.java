package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.ItemSubmissionGrading;
import judgels.sandalphon.api.submission.bundle.Verdict;

public class SetVerdictItemSubmissionGrader implements ItemSubmissionGrader {
    private final ObjectMapper objectMapper;
    private final Verdict verdict;

    public SetVerdictItemSubmissionGrader(ObjectMapper objectMapper, Verdict verdict) {
        this.objectMapper = objectMapper;
        this.verdict = verdict;
    }

    @Override
    public ItemSubmissionGrading grade(Item item, String answer) {
        return new ItemSubmissionGrading.Builder()
                .verdict(verdict)
                .score(Optional.empty())
                .build();
    }
}
