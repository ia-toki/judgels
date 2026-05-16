package judgels.submission.bundle;

import java.util.Optional;
import judgels.api.problem.bundle.Item;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.Verdict;

public class FixedVerdictItemSubmissionGrader implements ItemSubmissionGrader {
    private final Verdict verdict;

    public FixedVerdictItemSubmissionGrader(Verdict verdict) {
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
