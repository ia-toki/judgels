package judgels.sandalphon.submission.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import judgels.sandalphon.api.problem.bundle.StatementItemConfig;
import judgels.sandalphon.api.submission.bundle.Grading;
import judgels.sandalphon.api.submission.bundle.Verdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MultipleChoiceItemSubmissionGraderTests {
    private Item testItem1;
    private Item testItem2;

    @BeforeEach
    void before() {
        testItem1 = new Item.Builder()
                .jid("item1jid")
                .meta("1")
                .type(ItemType.MULTIPLE_CHOICE)
                .config(new MultipleChoiceItemConfig.Builder()
                        .statement("ini soal 1")
                        .score(4)
                        .penalty(-1)
                        .addChoices(
                                new MultipleChoiceItemConfig.Choice.Builder()
                                        .alias("a")
                                        .content("jawaban a")
                                        .isCorrect(false)
                                        .build(),
                                new MultipleChoiceItemConfig.Choice.Builder()
                                        .alias("b")
                                        .content("jawaban b (benar)")
                                        .isCorrect(true)
                                        .build(),
                                new MultipleChoiceItemConfig.Choice.Builder()
                                        .alias("c")
                                        .content("jawaban c")
                                        .isCorrect(false)
                                        .build(),
                                new MultipleChoiceItemConfig.Choice.Builder()
                                        .alias("d")
                                        .content("jawaban d")
                                        .isCorrect(false)
                                        .build(),
                                new MultipleChoiceItemConfig.Choice.Builder()
                                        .alias("e")
                                        .content("jawaban e")
                                        .isCorrect(false)
                                        .build()
                        )
                        .build())
                .build();

        testItem2 = new Item.Builder()
                .jid("item2jid")
                .meta("2")
                .type(ItemType.MULTIPLE_CHOICE)
                .config(new StatementItemConfig.Builder().statement("test statement").build())
                .build();
    }

    @Test
    void accepted() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader();

        Grading grading = grader.grade(testItem1, "b");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.ACCEPTED);
        assertThat(grading.getScore()).contains(4);
    }

    @Test
    void wrong_answer() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader();
        Grading grading;

        grading = grader.grade(testItem1, "a");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1);

        grading = grader.grade(testItem1, "c");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1);

        grading = grader.grade(testItem1, "an answer which is not included in item choices");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1);

        grading = grader.grade(testItem1, "");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(grading.getScore()).contains(-1);
    }

    @Test
    void internal_error() {
        MultipleChoiceItemSubmissionGrader grader = new MultipleChoiceItemSubmissionGrader();

        Grading grading = grader.grade(testItem2, "b");
        assertThat(grading.getVerdict()).isEqualTo(Verdict.INTERNAL_ERROR);
        assertThat(grading.getScore()).isEmpty();
    }
}
