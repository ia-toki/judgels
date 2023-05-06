package judgels.uriel.api.contest.problem;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.contest.module.ContestModuleType.VIRTUAL;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestProblemServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        assertPermitted(setProblems(adminHeader));
        assertPermitted(setProblems(managerHeader));
        assertPermitted(setProblems(supervisorAHeader));
        assertForbidden(setProblems(supervisorBHeader));
        assertForbidden(setProblems(contestantHeader));
        assertForbidden(setProblems(userHeader));
    }

    @Test
    void get_problems() {
        assertPermitted(getProblems(of(adminHeader)));
        assertPermitted(getProblems(of(managerHeader)));
        assertPermitted(getProblems(of(supervisorAHeader)));
        assertForbidden(getProblems(of(supervisorBHeader)));
        assertForbidden(getProblems(of(contestantHeader)));
        assertForbidden(getProblems(of(userHeader)));
        assertForbidden(getProblems(empty()));

        beginContest(contest);

        assertPermitted(getProblems(of(adminHeader)));
        assertPermitted(getProblems(of(managerHeader)));
        assertPermitted(getProblems(of(supervisorAHeader)));
        assertPermitted(getProblems(of(supervisorBHeader)));
        assertPermitted(getProblems(of(contestantHeader)));
        assertForbidden(getProblems(of(userHeader)));
        assertForbidden(getProblems(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(of(userHeader)));
        assertPermitted(getProblems(empty()));

        disableModule(contest, REGISTRATION);

        endContest(contest);

        assertPermitted(getProblems(of(adminHeader)));
        assertPermitted(getProblems(of(managerHeader)));
        assertPermitted(getProblems(of(supervisorAHeader)));
        assertPermitted(getProblems(of(supervisorBHeader)));
        assertPermitted(getProblems(of(contestantHeader)));
        assertForbidden(getProblems(of(userHeader)));
        assertForbidden(getProblems(empty()));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(of(userHeader)));
        assertPermitted(getProblems(empty()));
    }

    @Test
    void get_problems_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(getProblems(of(contestantHeader)));

        beginContest(contest);
        assertForbidden(getProblems(of(contestantHeader)));

        contestService.startVirtualContest(contestantHeader, contest.getJid());
        assertPermitted(getProblems(of(contestantHeader)));

        contestService.resetVirtualContest(managerHeader, contest.getJid());
        assertForbidden(getProblems(of(contestantHeader)));

        endContest(contest);
        assertPermitted(getProblems(of(contestantHeader)));
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
