package judgels.jerahmeel.api;

import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetErrors;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblem;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProblemSetProblemApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @BeforeEach
    void before() {
        contest1 = createContest(CONTEST_1_SLUG);
        contest2 = createContest(CONTEST_2_SLUG);
    }

    @Test
    void end_to_end_flow() {
        // as admin

        archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive")
                .name("Archive")
                .category("Category")
                .build());

        ProblemSet problemSetA = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problemset-a")
                .name("ProblemSet A")
                .archiveSlug("archive")
                .build());
        ProblemSet problemSetB = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problemset-b")
                .name("ProblemSet B")
                .archiveSlug("archive")
                .build());

        problemSetProblemClient.setProblems(adminToken, problemSetA.getJid(), List.of(
                new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(List.of(CONTEST_1_SLUG, CONTEST_2_SLUG))
                        .build(),
                new ProblemSetProblemData.Builder().alias("B").slug(PROBLEM_2_SLUG).type(PROGRAMMING).build())
        );

        assertForbidden(() -> problemSetProblemClient
                .setProblems(adminToken, problemSetA.getJid(), List.of(
                        new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(List.of("bogus"))
                        .build())))
                .hasMessageContaining(ProblemSetErrors.CONTEST_SLUGS_NOT_ALLOWED);

        var response = problemSetProblemClient.getProblems(adminToken, problemSetA.getJid());

        assertThat(response.getData()).containsExactly(
                new ProblemSetProblem.Builder()
                        .alias("A")
                        .problemJid(problem1.getJid())
                        .type(PROGRAMMING)
                        .contestJids(List.of(contest1.getJid(), contest2.getJid()))
                        .build(),
                new ProblemSetProblem.Builder().alias("B").problemJid(problem2.getJid()).type(PROGRAMMING).build());

        response = problemSetProblemClient.getProblems(adminToken, problemSetB.getJid());
        assertThat(response.getData()).isEmpty();

        // as user

        assertForbidden(() -> problemSetProblemClient
                .setProblems(userToken, problemSetA.getJid(), List.of()));

        response = problemSetProblemClient.getProblems(userToken, problemSetA.getJid());
        assertThat(response.getData()).hasSize(2);
    }
}
