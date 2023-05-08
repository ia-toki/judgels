package judgels.jerahmeel.api.problemset.problem;

import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_SLUG;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import judgels.jerahmeel.api.BaseJerahmeelServiceIntegrationTests;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetErrors;
import org.junit.jupiter.api.Test;

class ProblemSetProblemServiceIntegrationTests extends BaseJerahmeelServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        archiveService.createArchive(adminHeader, new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());

        ProblemSet problemSetA = problemSetService.createProblemSet(adminHeader, new ProblemSetCreateData.Builder()
                .slug("problemset-a")
                .name("ProblemSet A")
                .archiveSlug("archive")
                .build());
        ProblemSet problemSetB = problemSetService.createProblemSet(adminHeader, new ProblemSetCreateData.Builder()
                .slug("problemset-b")
                .name("ProblemSet B")
                .archiveSlug("archive")
                .build());

        problemSetProblemService.setProblems(adminHeader, problemSetA.getJid(), ImmutableList.of(
                new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of(CONTEST_1_SLUG, CONTEST_2_SLUG))
                        .build(),
                new ProblemSetProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(PROGRAMMING).build())
        );

        assertThatThrownBy(() -> problemSetProblemService
                .setProblems(adminHeader, problemSetA.getJid(), ImmutableList.of(
                        new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of("bogus"))
                        .build())))
                .hasFieldOrPropertyWithValue("code", 403)
                .hasMessageContaining(ProblemSetErrors.CONTEST_SLUGS_NOT_ALLOWED);

        ProblemSetProblemsResponse response =
                problemSetProblemService.getProblems(Optional.of(adminHeader), problemSetA.getJid());

        assertThat(response.getData()).containsExactly(
                new ProblemSetProblem.Builder()
                        .alias("A")
                        .problemJid(problem1.getJid())
                        .type(PROGRAMMING)
                        .contestJids(ImmutableList.of(CONTEST_1_JID, CONTEST_2_JID))
                        .build(),
                new ProblemSetProblem.Builder().alias("B").problemJid(problem2.getJid()).type(PROGRAMMING).build()
        );

        response = problemSetProblemService.getProblems(Optional.of(adminHeader), problemSetB.getJid());
        assertThat(response.getData()).isEmpty();

        // as user

        assertThatThrownBy(() -> problemSetProblemService
                .setProblems(userHeader, problemSetA.getJid(), ImmutableList.of()))
                .hasFieldOrPropertyWithValue("code", 403);

        response = problemSetProblemService.getProblems(Optional.of(userHeader), problemSetA.getJid());

        assertThat(response.getData()).hasSize(2);
    }
}
