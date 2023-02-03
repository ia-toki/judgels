package judgels.jerahmeel.api.chapter.problem;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterCreateData;
import judgels.sandalphon.api.problem.ProblemType;
import org.junit.jupiter.api.Test;

class ChapterProblemServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Chapter chapterA = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter A")
                .build());
        Chapter chapterB = chapterService.createChapter(ADMIN_HEADER, new ChapterCreateData.Builder()
                .name("Chapter B")
                .build());

        chapterProblemService.setProblems(ADMIN_HEADER, chapterA.getJid(), ImmutableList.of(
                new ChapterProblemData.Builder().alias("A").slug(PROBLEM_1_SLUG).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(ProblemType.PROGRAMMING).build())
        );

        ChapterProblemsResponse response =
                chapterProblemService.getProblems(Optional.of(ADMIN_HEADER), chapterA.getJid());

        assertThat(response.getData()).containsExactly(
                new ChapterProblem.Builder().alias("A").problemJid(PROBLEM_1_JID).type(ProblemType.PROGRAMMING).build(),
                new ChapterProblem.Builder().alias("B").problemJid(PROBLEM_2_JID).type(ProblemType.PROGRAMMING).build()
        );

        response = chapterProblemService.getProblems(Optional.of(ADMIN_HEADER), chapterB.getJid());
        assertThat(response.getData()).isEmpty();

        // assert user

        assertThatThrownBy(() -> chapterProblemService
                .setProblems(USER_HEADER, chapterA.getJid(), ImmutableList.of()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = chapterProblemService.getProblems(Optional.of(USER_HEADER), chapterA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
