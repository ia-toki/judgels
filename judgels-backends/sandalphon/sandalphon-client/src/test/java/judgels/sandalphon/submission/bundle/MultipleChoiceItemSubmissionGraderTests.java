package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import judgels.gabriel.api.Verdicts;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.ItemSubmission.ItemSubmissionGrading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultipleChoiceItemSubmissionGraderTests {
    private ObjectMapper mapper;
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void before() {
        mapper = new ObjectMapper();

        testItem1 = new Item.Builder()
                .jid("item1jid")
                .meta("1")
                .type(ItemType.MULTIPLE_CHOICE)
                .config("{\"statement\":\"ini soal 1\",\"score\":4.0,\"penalty\":1.0,\""
                        + "choices\":[{\"alias\":\"a\",\"content\":\"jawaban a\",\"isCorrect\":false},"
                        + "{\"alias\":\"b\",\"content\":\"jawaban b (benar)\",\"isCorrect\":true},"
                        + "{\"alias\":\"c\",\"content\":\"jawaban c\",\"isCorrect\":false},"
                        + "{\"alias\":\"d\",\"content\":\"jawaban d\",\"isCorrect\":false},"
                        + "{\"alias\":\"e\",\"content\":\"jawaban e\",\"isCorrect\":false}]}")
                .build();

        testItem2 = new Item.Builder()
                .jid("item2jid")
                .meta("2")
                .type(ItemType.MULTIPLE_CHOICE)
                .config("Invalid JSON")
                .build();
    }

    @Test
    void accepted() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader(mapper);

        ItemSubmissionGrading grading = grader.grade(testItem1, "b");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.ACCEPTED.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.ACCEPTED.getName());
        assertThat(grading.getScore().isPresent()).isTrue();
        assertThat(grading.getScore().get()).isEqualTo(4);
    }

    @Test
    void wrong_answer() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader(mapper);
        ItemSubmissionGrading grading;

        grading = grader.grade(testItem1, "a");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.WRONG_ANSWER.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.WRONG_ANSWER.getName());
        assertThat(grading.getScore().isPresent()).isTrue();
        assertThat(grading.getScore().get()).isEqualTo(-1);

        grading = grader.grade(testItem1, "c");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.WRONG_ANSWER.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.WRONG_ANSWER.getName());
        assertThat(grading.getScore().isPresent()).isTrue();
        assertThat(grading.getScore().get()).isEqualTo(-1);

        grading = grader.grade(testItem1, "an answer which is not included in item choices");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.WRONG_ANSWER.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.WRONG_ANSWER.getName());
        assertThat(grading.getScore().isPresent()).isTrue();
        assertThat(grading.getScore().get()).isEqualTo(-1);

        grading = grader.grade(testItem1, "");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.WRONG_ANSWER.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.WRONG_ANSWER.getName());
        assertThat(grading.getScore().isPresent()).isTrue();
        assertThat(grading.getScore().get()).isEqualTo(-1);
    }

    @Test
    void internal_error() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader(mapper);

        ItemSubmissionGrading grading = grader.grade(testItem2, "b");
        assertThat(grading.getVerdict().getCode()).isEqualTo(Verdicts.INTERNAL_ERROR.getCode());
        assertThat(grading.getVerdict().getName()).isEqualTo(Verdicts.INTERNAL_ERROR.getName());
        assertThat(grading.getScore().isPresent()).isFalse();
    }
}
