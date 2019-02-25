package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedVerdictItemSubmissionGraderTests {
    private ObjectMapper mapper;
    private Item item;

    @BeforeEach
    void before() {
        mapper = new ObjectMapper();
        item = new Item.Builder()
                .jid("item1jid")
                .meta("1")
                .type(ItemType.MULTIPLE_CHOICE)
                .config("")
                .build();
    }

    @Test
    void pending_manual_grading() {
        FixedVerdictItemSubmissionGrader grader = new FixedVerdictItemSubmissionGrader(
                mapper, Verdict.PENDING_MANUAL_GRADING);

        Grading grading = grader.grade(item, "any answer");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.PENDING_MANUAL_GRADING);
        assertThat(grading.getScore()).isEmpty();
    }

    @Test
    void grading_not_needed() {
        FixedVerdictItemSubmissionGrader grader = new FixedVerdictItemSubmissionGrader(
                mapper, Verdict.GRADING_NOT_NEEDED);

        Grading grading = grader.grade(item, "any answer");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.GRADING_NOT_NEEDED);
        assertThat(grading.getScore()).isEmpty();
    }
}
