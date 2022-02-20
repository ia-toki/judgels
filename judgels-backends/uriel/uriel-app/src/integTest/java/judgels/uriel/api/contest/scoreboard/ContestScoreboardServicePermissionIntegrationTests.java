package judgels.uriel.api.contest.scoreboard;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_2_SLUG;

import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestScoreboardServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
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
        assertPermitted(getScoreboard(of(ADMIN_HEADER)));
        assertPermitted(getScoreboard(of(MANAGER_HEADER)));
        assertPermitted(getScoreboard(of(SUPERVISOR_A_HEADER)));
        assertPermitted(getScoreboard(of(SUPERVISOR_B_HEADER)));
        assertForbidden(getScoreboard(of(CONTESTANT_HEADER)));
        assertForbidden(getScoreboard(of(USER_HEADER)));
        assertForbidden(getScoreboard(empty()));

        beginContest(contest);

        assertPermitted(getScoreboard(of(ADMIN_HEADER)));
        assertPermitted(getScoreboard(of(MANAGER_HEADER)));
        assertPermitted(getScoreboard(of(SUPERVISOR_A_HEADER)));
        assertPermitted(getScoreboard(of(SUPERVISOR_B_HEADER)));
        assertPermitted(getScoreboard(of(CONTESTANT_HEADER)));
        assertForbidden(getScoreboard(of(USER_HEADER)));
        assertForbidden(getScoreboard(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getScoreboard(of(USER_HEADER)));
        assertPermitted(getScoreboard(empty()));
    }

    @Test
    void get_scoreboard__show_closed_problems() {
        beginContest(contest);

        assertPermitted(getScoreboardShowClosedProblems(of(ADMIN_HEADER)));
        assertPermitted(getScoreboardShowClosedProblems(of(MANAGER_HEADER)));
        assertPermitted(getScoreboardShowClosedProblems(of(SUPERVISOR_A_HEADER)));
        assertPermitted(getScoreboardShowClosedProblems(of(SUPERVISOR_B_HEADER)));
        assertForbidden(getScoreboardShowClosedProblems(of(CONTESTANT_HEADER)));
        assertForbidden(getScoreboardShowClosedProblems(of(USER_HEADER)));
        assertForbidden(getScoreboardShowClosedProblems(empty()));
    }

    @Test
    void refresh_scoreboard() {
        assertPermitted(refreshScoreboard(ADMIN_HEADER));
        assertPermitted(refreshScoreboard(MANAGER_HEADER));
        assertPermitted(refreshScoreboard(SUPERVISOR_A_HEADER));
        assertForbidden(refreshScoreboard(SUPERVISOR_B_HEADER));
        assertForbidden(refreshScoreboard(CONTESTANT_HEADER));
        assertForbidden(refreshScoreboard(USER_HEADER));
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
