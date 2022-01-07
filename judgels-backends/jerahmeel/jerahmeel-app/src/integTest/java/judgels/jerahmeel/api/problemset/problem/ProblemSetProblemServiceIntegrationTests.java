package judgels.jerahmeel.api.problemset.problem;

import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_SLUG;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetErrors;
import org.junit.jupiter.api.Test;

class ProblemSetProblemServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        archiveService.createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());

        ProblemSet problemSetA = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problemset-a")
                .name("ProblemSet A")
                .archiveSlug("archive")
                .build());
        ProblemSet problemSetB = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problemset-b")
                .name("ProblemSet B")
                .archiveSlug("archive")
                .build());

        problemSetProblemService.setProblems(ADMIN_HEADER, problemSetA.getJid(), ImmutableList.of(
                new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of(CONTEST_1_SLUG, CONTEST_2_SLUG))
                        .build(),
                new ProblemSetProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(PROGRAMMING).build())
        );

        assertThatThrownBy(() -> problemSetProblemService
                .setProblems(ADMIN_HEADER, problemSetA.getJid(), ImmutableList.of(
                        new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of("bogus"))
                        .build())))
                .hasFieldOrPropertyWithValue("code", 403)
                .hasMessageContaining(ProblemSetErrors.CONTEST_SLUGS_NOT_ALLOWED);

        ProblemSetProblemsResponse response =
                problemSetProblemService.getProblems(Optional.of(ADMIN_HEADER), problemSetA.getJid());

        assertThat(response.getData()).containsExactly(
                new ProblemSetProblem.Builder()
                        .alias("A")
                        .problemJid(PROBLEM_1_JID)
                        .type(PROGRAMMING)
                        .contestJids(ImmutableList.of(CONTEST_1_JID, CONTEST_2_JID))
                        .build(),
                new ProblemSetProblem.Builder().alias("B").problemJid(PROBLEM_2_JID).type(PROGRAMMING).build()
        );

        response = problemSetProblemService.getProblems(Optional.of(ADMIN_HEADER), problemSetB.getJid());
        assertThat(response.getData()).isEmpty();

        // as user

        assertThatThrownBy(() -> problemSetProblemService
                .setProblems(USER_HEADER, problemSetA.getJid(), ImmutableList.of()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = problemSetProblemService.getProblems(Optional.of(USER_HEADER), problemSetA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
