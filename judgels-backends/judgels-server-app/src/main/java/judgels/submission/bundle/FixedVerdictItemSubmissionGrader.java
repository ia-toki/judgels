package judgels.submission.bundle;

import java.util.Optional;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.submission.bundle.Grading;
import judgels.api.submission.bundle.ItemVerdict;

public class FixedVerdictItemSubmissionGrader implements ItemSubmissionGrader {
    private final ItemVerdict verdict;

    public FixedVerdictItemSubmissionGrader(ItemVerdict verdict) {
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
