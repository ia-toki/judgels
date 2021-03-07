package judgels.jerahmeel.api.problemset;

import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static judgels.jerahmeel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.jerahmeel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_1_SLUG;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_JID;
import static judgels.jerahmeel.api.mocks.MockUriel.CONTEST_2_SLUG;
import static judgels.jerahmeel.api.problemset.ProblemSetErrors.ARCHIVE_SLUG_NOT_FOUND;
import static judgels.jerahmeel.api.problemset.ProblemSetErrors.SLUG_ALREADY_EXISTS;
import static judgels.sandalphon.api.problem.ProblemType.PROGRAMMING;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.time.Instant;
import java.util.Optional;
import judgels.jerahmeel.api.AbstractTrainingServiceIntegrationTests;
import judgels.jerahmeel.api.archive.Archive;
import judgels.jerahmeel.api.archive.ArchiveCreateData;
import judgels.jerahmeel.api.problemset.problem.ProblemSetProblemData;
import org.junit.jupiter.api.Test;

class ProblemSetServiceIntegrationTests extends AbstractTrainingServiceIntegrationTests {
    @Test
    void end_to_end_flow() {
        // as admin

        Archive archiveA = archiveService.createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                .slug("archive-a")
                .name("Archive A")
                .category("Category")
                .build());

        Archive archiveB = archiveService.createArchive(ADMIN_HEADER, new ArchiveCreateData.Builder()
                .slug("archive-b")
                .name("Archive B")
                .category("Category")
                .build());

        ProblemSet problemSet1 = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-1")
                .name("Problem Set 1")
                .archiveSlug(archiveA.getSlug())
                .description("This is problem set 1")
                .contestTime(Instant.ofEpochMilli(1))
                .build());

        ProblemSet problemSet2A = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-2a")
                .name("Problem Set 2A")
                .archiveSlug(archiveA.getSlug())
                .contestTime(Instant.ofEpochMilli(2))
                .build());

        ProblemSet problemSet2B = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-2b")
                .name("Problem Set 2B")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(2))
                .build());

        ProblemSet problemSet9 = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-9")
                .name("Problem Set 9")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(9))
                .build());

        ProblemSet problemSet10 = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-10")
                .name("Problem Set 10")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(10))
                .build());

        ProblemSet problemSet0 = problemSetService.createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                .slug("problem-set-0")
                .name("Problem Set 0")
                .archiveSlug(archiveB.getSlug())
                .contestTime(Instant.ofEpochMilli(0))
                .build());

        problemSetProblemService.setProblems(ADMIN_HEADER, problemSet1.getJid(), ImmutableList.of(
                new ProblemSetProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of(CONTEST_1_SLUG))
                        .build()));

        problemSetProblemService.setProblems(ADMIN_HEADER, problemSet2A.getJid(), ImmutableList.of(
                new ProblemSetProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .type(PROGRAMMING)
                        .contestSlugs(ImmutableList.of(CONTEST_2_SLUG))
                        .build()));

        assertThat(problemSet1.getSlug()).isEqualTo("problem-set-1");
        assertThat(problemSet1.getName()).isEqualTo("Problem Set 1");
        assertThat(problemSet1.getArchiveJid()).isEqualTo(archiveA.getJid());
        assertThat(problemSet1.getDescription()).isEqualTo("This is problem set 1");

        assertThat(problemSet2A.getSlug()).isEqualTo("problem-set-2a");

        assertThatRemoteExceptionThrownBy(() -> problemSetService
                .createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                        .slug("problem-set-1")
                        .name("Problem Set 1")
                        .archiveSlug(archiveA.getSlug())
                        .build()))
                .isGeneratedFromErrorType(SLUG_ALREADY_EXISTS);

        assertThatRemoteExceptionThrownBy(() -> problemSetService
                .createProblemSet(ADMIN_HEADER, new ProblemSetCreateData.Builder()
                        .slug("problem-set-3")
                        .name("Problem Set 3")
                        .archiveSlug("bogus")
                        .build()))
                .isGeneratedFromErrorType(ARCHIVE_SLUG_NOT_FOUND);

        assertThatRemoteExceptionThrownBy(() -> problemSetService
                .updateProblemSet(ADMIN_HEADER, problemSet2A.getJid(), new ProblemSetUpdateData.Builder()
                        .slug("problem-set-1")
                        .build()))
                .isGeneratedFromErrorType(SLUG_ALREADY_EXISTS);

        assertThatRemoteExceptionThrownBy(() -> problemSetService
                .updateProblemSet(ADMIN_HEADER, problemSet2A.getJid(), new ProblemSetUpdateData.Builder()
                        .archiveSlug("bogus")
                        .build()))
                .isGeneratedFromErrorType(ARCHIVE_SLUG_NOT_FOUND);

        ProblemSetsResponse response = problemSetService.getProblemSets(
                Optional.of(ADMIN_HEADER), Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(response.getData().getPage()).containsExactly(
                problemSet0, problemSet10, problemSet9, problemSet2B, problemSet2A, problemSet1);

        // as user

        assertThatRemoteExceptionThrownBy(() -> problemSetService
                .createProblemSet(USER_HEADER, new ProblemSetCreateData.Builder()
                        .slug("problem-set-3")
                        .name("Problem Set 3")
                        .archiveSlug(archiveA.getSlug())
                        .build()))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        response = problemSetService.getProblemSets(
                Optional.of(USER_HEADER), Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(response.getData().getPage()).containsExactly(
                problemSet0, problemSet10, problemSet9, problemSet2B, problemSet2A, problemSet1);

        response = problemSetService.getProblemSets(
                Optional.of(ADMIN_HEADER), Optional.of("archive-a"), Optional.empty(), Optional.empty());
        assertThat(response.getData().getPage()).containsExactly(problemSet2A, problemSet1);

        response = problemSetService.getProblemSets(
                Optional.of(ADMIN_HEADER), Optional.of("archive-b"), Optional.empty(), Optional.empty());
        assertThat(response.getData().getPage()).containsExactly(problemSet10, problemSet9, problemSet2B, problemSet0);

        response = problemSetService.getProblemSets(
                Optional.of(ADMIN_HEADER), Optional.empty(), Optional.of("Set 2"), Optional.empty());
        assertThat(response.getData().getPage()).containsExactly(problemSet2B, problemSet2A);

        assertThat(problemSetService.searchProblemSet(CONTEST_1_JID)).isEqualTo(problemSet1);
        assertThat(problemSetService.searchProblemSet(CONTEST_2_JID)).isEqualTo(problemSet2A);
        assertThatRemoteExceptionThrownBy(() -> problemSetService.searchProblemSet("bogus"))
                .isGeneratedFromErrorType(ErrorType.NOT_FOUND);
    }
}
