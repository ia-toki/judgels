package judgels.jerahmeel.api;

import static judgels.jerahmeel.api.problemset.ProblemSetErrors.ARCHIVE_SLUG_NOT_FOUND;
import static judgels.jerahmeel.api.problemset.ProblemSetErrors.SLUG_ALREADY_EXISTS;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import judgels.jerahmeel.ProblemSetClient;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.problemset.ProblemSet;
import judgels.jerahmeel.api.problemset.ProblemSetCreateData;
import judgels.jerahmeel.api.problemset.ProblemSetUpdateData;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProblemSetApiIntegrationTests extends BaseJerahmeelApiIntegrationTests {
    @BeforeEach
    void before() {
        contest1 = createContest(CONTEST_1_SLUG);
        contest2 = createContest(CONTEST_2_SLUG);
    }

    @Test
    void end_to_end_flow() {
        // as admin

        Archive archiveA = archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive-a")
                .name("Archive A")
                .category("Category")
                .description("Written by [user:userA]")
                .build());

        Archive archiveB = archiveClient.createArchive(adminToken, new ArchiveCreateData.Builder()
                .slug("archive-b")
                .name("Archive B")
                .category("Category")
                .build());

        ProblemSet problemSet1 = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-1")
                .name("Problem Set 1")
                .archiveSlug(archiveA.getSlug())
                .description("This is problem set 1 written by [user:userB]")
                .contestTime(Instant.ofEpochMilli(1))
                .build());

        ProblemSet problemSet2A = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-2a")
                .name("Problem Set 2A")
                .archiveSlug(archiveA.getSlug())
                .contestTime(Instant.ofEpochMilli(2))
                .build());

        ProblemSet problemSet2B = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-2b")
                .name("Problem Set 2B")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(2))
                .build());

        ProblemSet problemSet9 = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-9")
                .name("Problem Set 9")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(9))
                .build());

        ProblemSet problemSet10 = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-10")
                .name("Problem Set 10")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(10))
                .build());

        ProblemSet problemSet0 = problemSetClient.createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                .slug("problem-set-0")
                .name("Problem Set 0")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(0))
                .build());

        problemSetProblemClient.setProblems(adminToken, problemSet1.getJid(), List.of(
                new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(List.of(CONTEST_1_SLUG))
                        .build()));

        problemSetProblemClient.setProblems(adminToken, problemSet2A.getJid(), List.of(
                new ProblemSetProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(List.of(CONTEST_2_SLUG))
                        .build()));

        assertThat(problemSet1.getSlug()).isEqualTo("problem-set-1");
        assertThat(problemSet1.getName()).isEqualTo("Problem Set 1");
        assertThat(problemSet1.getArchiveJid()).isEqualTo(archiveA.getJid());
        assertThat(problemSet1.getDescription()).isEqualTo("This is problem set 1 written by [user:userB]");

        assertThat(problemSet2A.getSlug()).isEqualTo("problem-set-2a");

        assertBadRequest(() -> problemSetClient
                .createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                        .slug("problem-set-1")
                        .name("Problem Set 1")
                        .archiveSlug(archiveA.getSlug())
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> problemSetClient
                .createProblemSet(adminToken, new ProblemSetCreateData.Builder()
                        .slug("problem-set-3")
                        .name("Problem Set 3")
                        .archiveSlug("bogus")
                        .build()))
                .hasMessageContaining(ARCHIVE_SLUG_NOT_FOUND);

        assertBadRequest(() -> problemSetClient
                .updateProblemSet(adminToken, problemSet2A.getJid(), new ProblemSetUpdateData.Builder()
                        .slug("problem-set-1")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> problemSetClient
                .updateProblemSet(adminToken, problemSet2A.getJid(), new ProblemSetUpdateData.Builder()
                        .archiveSlug("bogus")
                        .build()))
                .hasMessageContaining(ARCHIVE_SLUG_NOT_FOUND);

        var response = problemSetClient.getProblemSets(adminToken, null);
        assertThat(response.getData().getPage()).containsExactly(
                problemSet0, problemSet10, problemSet9, problemSet2B, problemSet2A, problemSet1);
        assertThat(response.getProfilesMap()).containsKeys(userA.getJid(), userB.getJid());

        // as user

        assertForbidden(() -> problemSetClient
                .createProblemSet(userToken, new ProblemSetCreateData.Builder()
                        .slug("problem-set-3")
                        .name("Problem Set 3")
                        .archiveSlug(archiveA.getSlug())
                        .build()));

        response = problemSetClient.getProblemSets(userToken, null);
        assertThat(response.getData().getPage()).containsExactly(
                problemSet0, problemSet10, problemSet9, problemSet2B, problemSet2A, problemSet1);

        var params = new ProblemSetClient.GetProblemSetsParams();
        params.archiveSlug = "archive-a";
        response = problemSetClient.getProblemSets(adminToken, params);
        assertThat(response.getData().getPage()).containsExactly(problemSet2A, problemSet1);

        params = new ProblemSetClient.GetProblemSetsParams();
        params.archiveSlug = "archive-b";
        response = problemSetClient.getProblemSets(adminToken, params);
        assertThat(response.getData().getPage()).containsExactly(problemSet10, problemSet9, problemSet2B, problemSet0);

        params = new ProblemSetClient.GetProblemSetsParams();
        params.name = "Set 2";
        response = problemSetClient.getProblemSets(adminToken, params);
        assertThat(response.getData().getPage()).containsExactly(problemSet2B, problemSet2A);

        assertThat(problemSetClient.searchProblemSet(contest1.getJid())).isEqualTo(problemSet1);
        assertThat(problemSetClient.searchProblemSet(contest2.getJid())).isEqualTo(problemSet2A);
        assertNotFound(() -> problemSetClient.searchProblemSet("bogus"));
    }
}
