package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedVerdictItemSubmissionGraderTests {
    private Item item;

    @BeforeEach
    void before() {
        item = new Item.Builder()
                .jid("item1jid")
                .meta("1")
                .type(ItemType.MULTIPLE_CHOICE)
                .config(new StatementItemConfig.Builder().statement("test statement").build())
                .build();
    }

    @Test
    void pending_manual_grading() {
        FixedVerdictItemSubmissionGrader grader = new FixedVerdictItemSubmissionGrader(Verdict.PENDING_MANUAL_GRADING);

        Grading grading = grader.grade(item, "any answer");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.PENDING_MANUAL_GRADING);
        assertThat(grading.getScore()).isEmpty();
    }
}
