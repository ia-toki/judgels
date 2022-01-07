package judgels.uriel.api.contest.scoreboard;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestUpdateData;
import judgels.uriel.api.contest.module.ContestModuleType;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.problem.ContestProblemService;
import org.junit.jupiter.api.Test;

class ContestScoreboardServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private ContestScoreboardService scoreboardService = createService(ContestScoreboardService.class);
    private ContestProblemService problemService = createService(ContestProblemService.class);

    @Test
    void end_to_end_flow() {
        Contest contest = createContestWithRoles("contest");

        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build()));


        // as manager

        scoreboardService.refreshScoreboard(MANAGER_HEADER, contest.getJid());
        await().atMost(1, TimeUnit.SECONDS).pollInterval(300, TimeUnit.MILLISECONDS).until(() ->
                scoreboardService.getScoreboard(of(MANAGER_HEADER), contest.getJid(), false, false, empty())
                .isPresent());

        ContestScoreboardResponse response =
                scoreboardService.getScoreboard(of(MANAGER_HEADER), contest.getJid(), false, false, empty()).get();

        assertThat(response.getData().getType()).isEqualTo(ContestScoreboardType.OFFICIAL);

        ContestScoreboardConfig config = response.getConfig();
        assertThat(config.getCanViewOfficialAndFrozen()).isFalse();
        assertThat(config.getCanViewClosedProblems()).isFalse();
        assertThat(config.getCanRefresh()).isTrue();


        contestService.updateContest(ADMIN_HEADER, contest.getJid(), new ContestUpdateData.Builder()
                .beginTime(Instant.now().minus(Duration.ofHours(5)))
                .build());

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.FROZEN_SCOREBOARD);

        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(CLOSED)
                        .build()));

        scoreboardService.refreshScoreboard(MANAGER_HEADER, contest.getJid());
        await().atMost(1, TimeUnit.SECONDS).pollInterval(300, TimeUnit.MILLISECONDS).until(() ->
                scoreboardService.getScoreboard(of(MANAGER_HEADER), contest.getJid(), true, false, empty())
                        .get().getData().getType() == ContestScoreboardType.FROZEN);

        response =
                scoreboardService.getScoreboard(of(MANAGER_HEADER), contest.getJid(), true, true, empty()).get();

        config = response.getConfig();
        assertThat(config.getCanViewOfficialAndFrozen()).isTrue();
        assertThat(config.getCanViewClosedProblems()).isTrue();
        assertThat(config.getCanRefresh()).isTrue();


        // as supervisor

        response =
                scoreboardService.getScoreboard(of(SUPERVISOR_HEADER), contest.getJid(), false, false, empty()).get();
        assertThat(response.getConfig().getCanViewOfficialAndFrozen()).isTrue();
        assertThat(response.getConfig().getCanRefresh()).isFalse();


        // as contestant

        response =
                scoreboardService.getScoreboard(of(CONTESTANT_HEADER), contest.getJid(), false, false, empty()).get();
        assertThat(response.getConfig().getCanViewOfficialAndFrozen()).isFalse();
        assertThat(response.getConfig().getCanRefresh()).isFalse();


        // as user

        assertThatThrownBy(() -> scoreboardService
                .getScoreboard(of(USER_HEADER), contest.getJid(), false, false, empty()))
                .hasFieldOrPropertyWithValue("code", 403);


        // as guest

        assertThatThrownBy(() -> scoreboardService
                .getScoreboard(empty(), contest.getJid(), false, false, empty()))
                .hasFieldOrPropertyWithValue("code", 403);


        // as user and guest for open contests

        moduleService.enableModule(ADMIN_HEADER, contest.getJid(), ContestModuleType.REGISTRATION);

        assertThat(scoreboardService.getScoreboard(of(USER_HEADER), contest.getJid(), false, false, empty()))
                .isPresent();

        assertThat(scoreboardService.getScoreboard(empty(), contest.getJid(), false, false, empty()))
                .isPresent();
    }
}
