package judgels.uriel.api.contest.scoreboard;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_JID;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardServiceIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestScoreboardService scoreboardService = createService(ContestScoreboardService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .style(ContestStyle.IOI)
                .begun()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.SCOREBOARD)
                .supervisors(SUPERVISOR_B)
                .modules(REGISTRATION)
                .problems("A", PROBLEM_1_SLUG, "B", PROBLEM_2_SLUG)
                .build();
    }

    @Test
    void get_scoreboard__official() {
        refreshUntilScoreboardPresent(ContestScoreboardType.OFFICIAL);

        Map<Optional<AuthHeader>, Boolean> canRefreshMap = new LinkedHashMap<>();
        canRefreshMap.put(of(ADMIN_HEADER), true);
        canRefreshMap.put(of(MANAGER_HEADER), true);
        canRefreshMap.put(of(SUPERVISOR_A_HEADER), true);
        canRefreshMap.put(of(SUPERVISOR_B_HEADER), false);
        canRefreshMap.put(of(CONTESTANT_HEADER), false);
        canRefreshMap.put(of(USER_HEADER), false);
        canRefreshMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : canRefreshMap.keySet()) {
            ContestScoreboardResponse response =
                    scoreboardService.getScoreboard(authHeader, contest.getJid(), false, false, empty()).get();

            assertThat(response.getData().getType()).isEqualTo(ContestScoreboardType.OFFICIAL);

            ContestScoreboardConfig config = response.getConfig();
            assertThat(config.getCanViewOfficialAndFrozen()).isFalse();
            assertThat(config.getCanRefresh()).isEqualTo(canRefreshMap.get(authHeader));
        }
    }

    @Test
    void get_scoreboard__frozen() {
        endContest(contest);
        enableModule(contest, FROZEN_SCOREBOARD);

        refreshUntilScoreboardPresent(FROZEN);

        Map<Optional<AuthHeader>, Boolean> canRefreshMap = new LinkedHashMap<>();
        canRefreshMap.put(of(ADMIN_HEADER), true);
        canRefreshMap.put(of(MANAGER_HEADER), true);
        canRefreshMap.put(of(SUPERVISOR_A_HEADER), true);
        canRefreshMap.put(of(SUPERVISOR_B_HEADER), false);
        canRefreshMap.put(of(CONTESTANT_HEADER), false);
        canRefreshMap.put(of(USER_HEADER), false);
        canRefreshMap.put(empty(), false);

        Map<Optional<AuthHeader>, Boolean> canViewOfficialAndFrozenMap = new LinkedHashMap<>();
        canViewOfficialAndFrozenMap.put(of(ADMIN_HEADER), true);
        canViewOfficialAndFrozenMap.put(of(MANAGER_HEADER), true);
        canViewOfficialAndFrozenMap.put(of(SUPERVISOR_A_HEADER), true);
        canViewOfficialAndFrozenMap.put(of(SUPERVISOR_B_HEADER), true);
        canViewOfficialAndFrozenMap.put(of(CONTESTANT_HEADER), false);
        canViewOfficialAndFrozenMap.put(of(USER_HEADER), false);
        canViewOfficialAndFrozenMap.put(empty(), false);

        for (Optional<AuthHeader> authHeader : canRefreshMap.keySet()) {
            ContestScoreboardResponse response =
                    scoreboardService.getScoreboard(authHeader, contest.getJid(), true, false, empty()).get();

            assertThat(response.getData().getType()).isEqualTo(FROZEN);

            ContestScoreboardConfig config = response.getConfig();
            assertThat(config.getCanViewOfficialAndFrozen()).isEqualTo(canViewOfficialAndFrozenMap.get(authHeader));
            assertThat(config.getCanRefresh()).isEqualTo(canRefreshMap.get(authHeader));
        }
    }

    @Test
    void get_scoreboard__closed_problems() {
        problemService.setProblems(MANAGER_HEADER, contest.getJid(), ImmutableList.of(
                new ContestProblemData.Builder()
                        .alias("A")
                        .slug(PROBLEM_1_SLUG)
                        .status(OPEN)
                        .build(),
                new ContestProblemData.Builder()
                        .alias("B")
                        .slug(PROBLEM_2_SLUG)
                        .status(CLOSED)
                        .build()));

        refreshUntilScoreboardPresent(OFFICIAL);

        ContestScoreboardResponse response =
                scoreboardService.getScoreboard(of(SUPERVISOR_A_HEADER), contest.getJid(), false, false, empty()).get();

        assertThat(response.getData().getScoreboard().getState().getProblemJids())
                .containsExactly(PROBLEM_1_JID);

        response =
                scoreboardService.getScoreboard(of(SUPERVISOR_A_HEADER), contest.getJid(), false, true, empty()).get();

        assertThat(response.getData().getScoreboard().getState().getProblemJids())
                .containsExactly(PROBLEM_1_JID, PROBLEM_2_JID);
    }

    private void refreshUntilScoreboardPresent(ContestScoreboardType type) {
        scoreboardService.refreshScoreboard(MANAGER_HEADER, contest.getJid());
        await().atMost(1, TimeUnit.SECONDS).pollInterval(300, TimeUnit.MILLISECONDS).until(() ->
                scoreboardService.getScoreboard(of(CONTESTANT_HEADER), contest.getJid(), true, false, empty())
                        .get().getData().getType() == type);
    }
}
