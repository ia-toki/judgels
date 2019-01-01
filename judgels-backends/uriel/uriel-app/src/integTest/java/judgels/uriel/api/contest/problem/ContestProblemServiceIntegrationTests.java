package judgels.uriel.api.contest.problem;

import static com.palantir.conjure.java.api.testing.Assertions.assertThat;
import static com.palantir.conjure.java.api.testing.Assertions.assertThatRemoteExceptionThrownBy;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;

import com.google.common.collect.ImmutableList;
import com.palantir.conjure.java.api.errors.ErrorType;
import java.util.List;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.junit.jupiter.api.Test;

class ContestProblemServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestProblemService problemService = createService(ContestProblemService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        // as manager

        ContestProblemsSetResponse setResponse =
                problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                        new ContestProblemData.Builder()
                            .alias("A")
                            .slug(PROBLEM_1_SLUG)
                            .status(OPEN)
                            .submissionsLimit(0)
                            .build()));
        assertThat(setResponse.getSetSlugs()).containsOnly(PROBLEM_1_SLUG);

        List<ContestProblemData> data = ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug("unknown-slug")
                        .status(OPEN)
                        .submissionsLimit(0)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("C")
                        .slug(PROBLEM_2_SLUG)
                        .status(ContestProblemStatus.CLOSED)
                        .submissionsLimit(0)
                        .build());
        setResponse = problemService.setProblems(MANAGER_HEADER, contest.getJid(), data);
        assertThat(setResponse.getSetSlugs()).containsOnly(PROBLEM_1_SLUG, PROBLEM_2_SLUG);

        ContestProblemsResponse response = problemService.getProblems(of(MANAGER_HEADER), contest.getJid());
        assertThat(response.getData()).containsOnly(
                new ContestProblem.Builder()
                        .alias("A")
                        .problemJid(PROBLEM_1_JID)
                        .status(OPEN)
                        .submissionsLimit(10)
                        .build(),
                new ContestProblem.Builder()
                        .alias("C")
                        .problemJid(PROBLEM_2_JID)
                        .status(ContestProblemStatus.CLOSED)
                        .submissionsLimit(0)
                        .build());
        assertThat(response.getProblemsMap().get(PROBLEM_1_JID).getSlug()).isEqualTo(PROBLEM_1_SLUG);
        assertThat(response.getTotalSubmissionsMap()).containsOnlyKeys(PROBLEM_1_JID, PROBLEM_2_JID);
        assertThat(response.getConfig().getCanManage()).isTrue();

        // as supervisor

        assertThatRemoteExceptionThrownBy(() -> problemService
                .setProblems(SUPERVISOR_HEADER, contest.getJid(), data))
                .isGeneratedFromErrorType(ErrorType.PERMISSION_DENIED);

        response = problemService.getProblems(of(SUPERVISOR_HEADER), contest.getJid());
        assertThat(response.getConfig().getCanManage()).isFalse();
    }
}
