package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;

public class FixedVerdictItemSubmissionGrader implements ItemSubmissionGrader {
    private final ObjectMapper objectMapper;
    private final Verdict verdict;

    public FixedVerdictItemSubmissionGrader(ObjectMapper objectMapper, Verdict verdict) {
        this.objectMapper = objectMapper;
        this.verdict = verdict;
    }

    @Override
    public Grading grade(Item item, String answer) {
        return new Grading.Builder()
                .verdict(verdict)
                .score(Optional.empty())
                .build();
    }
}
