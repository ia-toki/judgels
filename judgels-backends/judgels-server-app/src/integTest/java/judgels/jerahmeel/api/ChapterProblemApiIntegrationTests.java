package judgels.jerahmeel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.jerahmeel.api.chapter.problem.ChapterProblem;
import judgels.jerahmeel.api.chapter.problem.ChapterProblemData;
import judgels.sandalphon.api.problem.ProblemType;
import org.junit.jupiter.api.Test;

class ChapterProblemApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterClient.createChapter(adminToken, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterProblemClient.setProblems(adminToken, chapterA.getJid(), List.of(
                new ChapterProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(ProblemType.PROGRAMMING).build()));

        var response = chapterProblemClient.getProblems(adminToken, chapterA.getJid());
        assertThat(response.getData()).containsExactly(
                new ChapterProblem.Builder().alias("A").problemJid(problem1.getJid()).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblem.Builder().alias("B").problemJid(problem2.getJid()).type(ProblemType.PROGRAMMING).build());

        response = chapterProblemClient.getProblems(adminToken, chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertForbidden(() -> chapterProblemClient
                .setProblems(userToken, chapterA.getJid(), List.of()));

        response = chapterProblemClient.getProblems(userToken, chapterA.getJid());
        assertThat(response.getData()).hasSize(2);
    }
}
