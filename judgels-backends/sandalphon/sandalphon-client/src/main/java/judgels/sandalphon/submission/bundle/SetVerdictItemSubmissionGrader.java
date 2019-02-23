package judgels.sandalphon.submission.bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.ItemSubmissionGrading;
import judgels.sandalphon.api.submission.bundle.Verdict;

public class SetVerdictItemSubmissionGrader implements ItemSubmissionGrader {
    private Verdict verdict;

    public SetVerdictItemSubmissionGrader(Verdict verdict) {
        this.verdict = verdict;
    }

    @Override
    public ItemSubmissionGrading grade(ObjectMapper mapper, Item item, String answer) {
        return new ItemSubmissionGrading.Builder()
                .verdict(verdict)
                .score(Optional.empty())
                .build();
    }
}
