package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.CLOSED;
import static judgels.uriel.api.contest.problem.ContestProblemStatus.OPEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.FROZEN;
import static judgels.uriel.api.contest.scoreboard.ContestScoreboardType.OFFICIAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import judgels.uriel.ContestScoreboardClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.problem.ContestProblemData;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardConfig;
import judgels.uriel.api.contest.scoreboard.ContestScoreboardType;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardApiIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestScoreboardClient scoreboardClient = createClient(ContestScoreboardClient.class);

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

        Map<String, Boolean> canRefreshMap = new LinkedHashMap<>();
        canRefreshMap.put(adminToken, true);
        canRefreshMap.put(managerToken, true);
        canRefreshMap.put(supervisorAToken, true);
        canRefreshMap.put(supervisorBToken, false);
        canRefreshMap.put(contestantToken, false);
        canRefreshMap.put(userToken, false);
        canRefreshMap.put("", false);

        for (String token : canRefreshMap.keySet()) {
            var response = scoreboardClient.getScoreboard(token, contest.getJid(), null).get();
            assertThat(response.getData().getType()).isEqualTo(ContestScoreboardType.OFFICIAL);

            ContestScoreboardConfig config = response.getConfig();
            assertThat(config.getCanViewOfficialAndFrozen()).isFalse();
            assertThat(config.getCanRefresh()).isEqualTo(canRefreshMap.get(token));
        }
    }

    @Test
    void get_scoreboard__frozen() {
        endContest(contest);
        enableModule(contest, FROZEN_SCOREBOARD);

        refreshUntilScoreboardPresent(FROZEN);

        Map<String, Boolean> canRefreshMap = new LinkedHashMap<>();
        canRefreshMap.put(adminToken, true);
        canRefreshMap.put(managerToken, true);
        canRefreshMap.put(supervisorAToken, true);
        canRefreshMap.put(supervisorBToken, false);
        canRefreshMap.put(contestantToken, false);
        canRefreshMap.put(userToken, false);
        canRefreshMap.put("", false);

        Map<String, Boolean> canViewOfficialAndFrozenMap = new LinkedHashMap<>();
        canViewOfficialAndFrozenMap.put(adminToken, true);
        canViewOfficialAndFrozenMap.put(managerToken, true);
        canViewOfficialAndFrozenMap.put(supervisorAToken, true);
        canViewOfficialAndFrozenMap.put(supervisorBToken, true);
        canViewOfficialAndFrozenMap.put(contestantToken, false);
        canViewOfficialAndFrozenMap.put(userToken, false);
        canViewOfficialAndFrozenMap.put("", false);

        for (String token : canRefreshMap.keySet()) {
            var params = new ContestScoreboardClient.GetScoreboardParams();
            params.frozen = true;

            var response = scoreboardClient.getScoreboard(token, contest.getJid(), params).get();
            assertThat(response.getData().getType()).isEqualTo(FROZEN);

            ContestScoreboardConfig config = response.getConfig();
            assertThat(config.getCanViewOfficialAndFrozen()).isEqualTo(canViewOfficialAndFrozenMap.get(token));
            assertThat(config.getCanRefresh()).isEqualTo(canRefreshMap.get(token));
        }
    }

    @Test
    void get_scoreboard__closed_problems() {
        problemClient.setProblems(managerToken, contest.getJid(), List.of(
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

        var response = scoreboardClient.getScoreboard(supervisorAToken, contest.getJid(), null).get();
        assertThat(response.getData().getScoreboard().getState().getProblemJids())
                .containsExactly(problem1.getJid());

        var params = new ContestScoreboardClient.GetScoreboardParams();
        params.showClosedProblems = true;
        response = scoreboardClient.getScoreboard(supervisorAToken, contest.getJid(), params).get();

        assertThat(response.getData().getScoreboard().getState().getProblemJids())
                .containsExactly(problem1.getJid(), problem2.getJid());
    }

    private void refreshUntilScoreboardPresent(ContestScoreboardType type) {
        scoreboardClient.refreshScoreboard(managerToken, contest.getJid());

        var params = new ContestScoreboardClient.GetScoreboardParams();
        params.frozen = true;
        await().atMost(1, TimeUnit.SECONDS).pollInterval(300, TimeUnit.MILLISECONDS).until(() ->
                scoreboardClient.getScoreboard(contestantToken, contest.getJid(), params)
                        .get().getData().getType() == type);
    }
}
