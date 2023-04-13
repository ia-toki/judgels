package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.ShortAnswerItemConfig;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ShortAnswerItemSubmissionGraderTests {
    private Item testItem1;
    private Item testItem2;
    private Item testItem3;
    private Item testItem4;
    private Item testItem5;

    @BeforeEach
    void before() {
        testItem1 = new Item.Builder()
                .jid("item1jid")
                .meta("1")
                .number(1)
                .type(ItemType.SHORT_ANSWER)
                .config(new ShortAnswerItemConfig.Builder()
                        .statement("99 + 24 = ...")
                        .score(1)
                        .penalty(0)
                        .inputValidationRegex("\\d+")
                        .gradingRegex("123")
                        .build())
                .build();

        testItem2 = new Item.Builder()
                .jid("item2jid")
                .meta("2")
                .type(ItemType.SHORT_ANSWER)
                .config(new ShortAnswerItemConfig.Builder()
                        .statement("x^2 = 4, x = ...")
                        .score(4)
                        .penalty(-1)
                        .inputValidationRegex("[0-9,]+")
                        .gradingRegex(",*(2,-2)|(-2,2),*")
                        .build())
                .build();

        testItem3 = new Item.Builder()
                .jid("item3jid")
                .meta("3")
                .number(3)
                .type(ItemType.MULTIPLE_CHOICE)
                .config(new StatementItemConfig.Builder().statement("test statement").build())
                .build();

        testItem4 = new Item.Builder()
                .jid("item4jid")
                .meta("4")
                .number(4)
                .type(ItemType.SHORT_ANSWER)
                .config(new ShortAnswerItemConfig.Builder()
                        .statement("test statement")
                        .score(1)
                        .penalty(-1)
                        .inputValidationRegex("\\d+")
                        .gradingRegex("(")
                        .build())
                .build();

        testItem5 = new Item.Builder()
                .jid("item5jid")
                .meta("5")
                .number(5)
                .type(ItemType.SHORT_ANSWER)
                .config(new ShortAnswerItemConfig.Builder()
                        .statement("test statement")
                        .score(1)
                        .penalty(-1)
                        .inputValidationRegex("\\d+")
                        .gradingRegex("")
                        .build())
                .build();
    }

    @Test
    void accepted() {
        ShortAnswerItemSubmissionGrader grader = new ShortAnswerItemSubmissionGrader();
        Grading grading;

        grading = grader.grade(testItem1, "123");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(grading.getScore()).contains(1.0);

        grading = grader.grade(testItem2, "2,-2");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(grading.getScore()).contains(4.0);

        grading = grader.grade(testItem2, "-2,2");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(grading.getScore()).contains(4.0);

        grading = grader.grade(testItem2, "-2,2,");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(grading.getScore()).contains(4.0);
    }

    @Test
    void wrong_answer() {
        ShortAnswerItemSubmissionGrader grader = new ShortAnswerItemSubmissionGrader();
        Grading grading;

        grading = grader.grade(testItem1, "99");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(0.0);

        grading = grader.grade(testItem1, "124");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(0.0);

        grading = grader.grade(testItem1, "1234");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(0.0);

        grading = grader.grade(testItem1, "1123");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(0.0);

        grading = grader.grade(testItem2, "2");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);

        grading = grader.grade(testItem2, "-2");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);

        grading = grader.grade(testItem2, "2,2");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);

        grading = grader.grade(testItem2, "2,");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);

        grading = grader.grade(testItem2, "-2,");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);

        grading = grader.grade(testItem2, "c");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1.0);
    }

    @Test
    void internal_error() {
        ShortAnswerItemSubmissionGrader grader = new ShortAnswerItemSubmissionGrader();
        Grading grading;

        grading = grader.grade(testItem3, "b");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.INTERNAL_ERROR);
        assertThat(grading.getScore()).isEmpty();

        grading = grader.grade(testItem4, "b");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.INTERNAL_ERROR);
        assertThat(grading.getScore()).isEmpty();
    }

    @Test
    void pending_manual_grading() {
        ShortAnswerItemSubmissionGrader grader = new ShortAnswerItemSubmissionGrader();
        Grading grading;

        grading = grader.grade(testItem5, "b");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.PENDING_MANUAL_GRADING);
        assertThat(grading.getScore()).isEmpty();
    }
}
