package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.api.problem.ProblemType;
import org.junit.jupiter.api.Test;
import tlx.api.chapter.Chapter;
import tlx.api.chapter.ChapterCreateData;
import tlx.api.chapter.problem.ChapterProblem;
import tlx.api.chapter.problem.ChapterProblemData;

class ChapterProblemApiIntegrationTests extends BaseTrainingApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterAdminClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterAdminClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterProblemAdminClient.setProblems(adminToken, chapterA.getJid(), List.of(
                new ChapterProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(ProblemType.PROGRAMMING).build()));

        var response = chapterProblemClient.getProblems(adminToken, chapterA.getJid());
        assertThat(response.getData()).containsExactly(
                new ChapterProblem.Builder().alias("A").problemJid(problem1.getJid()).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblem.Builder().alias("B").problemJid(problem2.getJid()).type(ProblemType.PROGRAMMING).build());

        response = chapterProblemClient.getProblems(adminToken, chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertForbidden(() -> chapterProblemAdminClient
                .setProblems(userToken, chapterA.getJid(), List.of()));

        response = chapterProblemClient.getProblems(userToken, chapterA.getJid());
        assertThat(response.getData()).hasSize(2);
    }
}
