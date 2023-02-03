package judgels.uriel.api.contest.problem;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestProblemServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.PROBLEM)
                .supervisors(SUPERVISOR_B)
                .problems("A", PROBLEM_1_SLUG)
                .build();
    }

    @Test
    void set_problems() {
        assertPermitted(setProblems(ADMIN_HEADER));
        assertPermitted(setProblems(MANAGER_HEADER));
        assertPermitted(setProblems(SUPERVISOR_A_HEADER));
        assertForbidden(setProblems(SUPERVISOR_B_HEADER));
        assertForbidden(setProblems(CONTESTANT_HEADER));
        assertForbidden(setProblems(USER_HEADER));
    }

    @Test
    void get_problems() {
        assertPermitted(getProblems(of(ADMIN_HEADER)));
        assertPermitted(getProblems(of(MANAGER_HEADER)));
        assertPermitted(getProblems(of(SUPERVISOR_A_HEADER)));
        assertForbidden(getProblems(of(SUPERVISOR_B_HEADER)));
        assertForbidden(getProblems(of(CONTESTANT_HEADER)));
        assertForbidden(getProblems(of(USER_HEADER)));
        assertForbidden(getProblems(empty()));

        beginContest(contest);

        assertPermitted(getProblems(of(ADMIN_HEADER)));
        assertPermitted(getProblems(of(MANAGER_HEADER)));
        assertPermitted(getProblems(of(SUPERVISOR_A_HEADER)));
        assertPermitted(getProblems(of(SUPERVISOR_B_HEADER)));
        assertPermitted(getProblems(of(CONTESTANT_HEADER)));
        assertForbidden(getProblems(of(USER_HEADER)));
        assertForbidden(getProblems(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(of(USER_HEADER)));
        assertPermitted(getProblems(empty()));

        disableModule(contest, REGISTRATION);

        endContest(contest);

        assertPermitted(getProblems(of(ADMIN_HEADER)));
        assertPermitted(getProblems(of(MANAGER_HEADER)));
        assertPermitted(getProblems(of(SUPERVISOR_A_HEADER)));
        assertPermitted(getProblems(of(SUPERVISOR_B_HEADER)));
        assertPermitted(getProblems(of(CONTESTANT_HEADER)));
        assertForbidden(getProblems(of(USER_HEADER)));
        assertForbidden(getProblems(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(of(USER_HEADER)));
        assertPermitted(getProblems(empty()));
    }

    @Test
    void get_problems_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(getProblems(of(CONTESTANT_HEADER)));

        beginContest(contest);
        assertForbidden(getProblems(of(CONTESTANT_HEADER)));

        contestService.startVirtualContest(CONTESTANT_HEADER, contest.getJid());
        assertPermitted(getProblems(of(CONTESTANT_HEADER)));

        contestService.resetVirtualContest(MANAGER_HEADER, contest.getJid());
        assertForbidden(getProblems(of(CONTESTANT_HEADER)));

        endContest(contest);
        assertPermitted(getProblems(of(CONTESTANT_HEADER)));
    }

    private ThrowingCallable setProblems(AuthHeader authHeader) {
        return () -> problemService.setProblems(authHeader, contest.getJid(), Collections.emptyList());
    }

    private ThrowingCallable getProblems(Optional<AuthHeader> authHeader) {
        return callAll(
                () -> problemService.getProblems(authHeader, contest.getJid()),
                () -> problemService.getProgrammingProblemWorksheet(authHeader, contest.getJid(), "A", empty()));
    }
}
