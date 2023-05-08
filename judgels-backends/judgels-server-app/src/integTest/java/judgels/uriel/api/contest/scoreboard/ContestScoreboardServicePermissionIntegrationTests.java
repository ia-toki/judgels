package judgels.uriel.api.contest.scoreboard;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestScoreboardService scoreboardService = createService(ContestScoreboardService.class);

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
        assertPermitted(getScoreboard(of(adminHeader)));
        assertPermitted(getScoreboard(of(managerHeader)));
        assertPermitted(getScoreboard(of(supervisorAHeader)));
        assertPermitted(getScoreboard(of(supervisorBHeader)));
        assertForbidden(getScoreboard(of(contestantHeader)));
        assertForbidden(getScoreboard(of(userHeader)));
        assertForbidden(getScoreboard(empty()));

        beginContest(contest);

        assertPermitted(getScoreboard(of(adminHeader)));
        assertPermitted(getScoreboard(of(managerHeader)));
        assertPermitted(getScoreboard(of(supervisorAHeader)));
        assertPermitted(getScoreboard(of(supervisorBHeader)));
        assertPermitted(getScoreboard(of(contestantHeader)));
        assertForbidden(getScoreboard(of(userHeader)));
        assertForbidden(getScoreboard(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getScoreboard(of(userHeader)));
        assertPermitted(getScoreboard(empty()));
    }

    @Test
    void get_scoreboard__show_closed_problems() {
        beginContest(contest);

        assertPermitted(getScoreboardShowClosedProblems(of(adminHeader)));
        assertPermitted(getScoreboardShowClosedProblems(of(managerHeader)));
        assertPermitted(getScoreboardShowClosedProblems(of(supervisorAHeader)));
        assertPermitted(getScoreboardShowClosedProblems(of(supervisorBHeader)));
        assertForbidden(getScoreboardShowClosedProblems(of(contestantHeader)));
        assertForbidden(getScoreboardShowClosedProblems(of(userHeader)));
        assertForbidden(getScoreboardShowClosedProblems(empty()));
    }

    @Test
    void refresh_scoreboard() {
        assertPermitted(refreshScoreboard(adminHeader));
        assertPermitted(refreshScoreboard(managerHeader));
        assertPermitted(refreshScoreboard(supervisorAHeader));
        assertForbidden(refreshScoreboard(supervisorBHeader));
        assertForbidden(refreshScoreboard(contestantHeader));
        assertForbidden(refreshScoreboard(userHeader));
    }

    private ThrowingCallable getScoreboard(Optional<AuthHeader> authHeader) {
        return () -> scoreboardService
                .getScoreboard(authHeader, contest.getJid(), false, false, empty());
    }

    private ThrowingCallable getScoreboardShowClosedProblems(Optional<AuthHeader> authHeader) {
        return () -> scoreboardService
                .getScoreboard(authHeader, contest.getJid(), false, true, empty());
    }

    private ThrowingCallable refreshScoreboard(AuthHeader authHeader) {
        return () -> scoreboardService.refreshScoreboard(authHeader, contest.getJid());
    }
}
