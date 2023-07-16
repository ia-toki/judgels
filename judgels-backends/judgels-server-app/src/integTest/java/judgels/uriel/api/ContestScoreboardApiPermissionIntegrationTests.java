package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import judgels.uriel.ContestScoreboardClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestScoreboardClient scoreboardClient = createClient(ContestScoreboardClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .style(ContestStyle.IOI)
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.SCOREBOARD)
                .supervisors(SUPERVISOR_B)
                .problems("A", PROBLEM_1_SLUG, "B", PROBLEM_2_SLUG)
                .build();
    }

    @Test
    void get_scoreboard() {
        assertPermitted(getScoreboard(adminToken));
        assertPermitted(getScoreboard(managerToken));
        assertPermitted(getScoreboard(supervisorAToken));
        assertPermitted(getScoreboard(supervisorBToken));
        assertForbidden(getScoreboard(contestantToken));
        assertForbidden(getScoreboard(userToken));
        assertForbidden(getScoreboard(""));

        beginContest(contest);

        assertPermitted(getScoreboard(adminToken));
        assertPermitted(getScoreboard(managerToken));
        assertPermitted(getScoreboard(supervisorAToken));
        assertPermitted(getScoreboard(supervisorBToken));
        assertPermitted(getScoreboard(contestantToken));
        assertForbidden(getScoreboard(userToken));
        assertForbidden(getScoreboard(""));

        enableModule(contest, REGISTRATION);

        assertPermitted(getScoreboard(userToken));
        assertPermitted(getScoreboard(""));
    }

    @Test
    void get_scoreboard__show_closed_problems() {
        beginContest(contest);

        assertPermitted(getScoreboardShowClosedProblems(adminToken));
        assertPermitted(getScoreboardShowClosedProblems(managerToken));
        assertPermitted(getScoreboardShowClosedProblems(supervisorAToken));
        assertPermitted(getScoreboardShowClosedProblems(supervisorBToken));
        assertForbidden(getScoreboardShowClosedProblems(contestantToken));
        assertForbidden(getScoreboardShowClosedProblems(userToken));
        assertForbidden(getScoreboardShowClosedProblems(""));
    }

    @Test
    void refresh_scoreboard() {
        assertPermitted(refreshScoreboard(adminToken));
        assertPermitted(refreshScoreboard(managerToken));
        assertPermitted(refreshScoreboard(supervisorAToken));
        assertForbidden(refreshScoreboard(supervisorBToken));
        assertForbidden(refreshScoreboard(contestantToken));
        assertForbidden(refreshScoreboard(userToken));
    }

    private ThrowingCallable getScoreboard(String token) {
        return () -> scoreboardClient.getScoreboard(token, contest.getJid(), null);
    }

    private ThrowingCallable getScoreboardShowClosedProblems(String token) {
        var params = new ContestScoreboardClient.GetScoreboardParams();
        params.showClosedProblems = true;
        return () -> scoreboardClient.getScoreboard(token, contest.getJid(), params);
    }

    private ThrowingCallable refreshScoreboard(String token) {
        return () -> scoreboardClient.refreshScoreboard(token, contest.getJid());
    }
}
