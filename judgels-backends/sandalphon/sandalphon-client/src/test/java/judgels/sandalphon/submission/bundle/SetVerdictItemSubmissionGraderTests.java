package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.Grading;
import judgels.sandalphon.api.submission.bundle.Verdicts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetVerdictItemSubmissionGraderTests {
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
        SetVerdictItemSubmissionGrader grader = new SetVerdictItemSubmissionGrader(
                mapper, Verdicts.PENDING_MANUAL_GRADING);

        Grading grading = grader.grade(item, "any answer");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.PENDING_MANUAL_GRADING.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.PENDING_MANUAL_GRADING.getName());
        assertThat(grading.getScore().isPresent()).isFalse();
    }

    @Test
    void grading_not_needed() {
        SetVerdictItemSubmissionGrader grader = new SetVerdictItemSubmissionGrader(
                mapper, Verdicts.GRADING_NOT_NEEDED);

        Grading grading = grader.grade(item, "any answer");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.GRADING_NOT_NEEDED.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.GRADING_NOT_NEEDED.getName());
        assertThat(grading.getScore().isPresent()).isFalse();
    }
}
