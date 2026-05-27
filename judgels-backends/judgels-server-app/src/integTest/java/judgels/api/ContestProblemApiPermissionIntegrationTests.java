package judgels.api;

import static judgels.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.api.contest.module.ContestModuleType.VIRTUAL;

import java.time.Duration;
import java.util.Collections;
import judgels.api.contest.Contest;
import judgels.api.contest.module.ContestModulesConfig;
import judgels.api.contest.module.VirtualModuleConfig;
import judgels.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestProblemApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
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
        assertPermitted(setProblems(adminToken));
        assertPermitted(setProblems(managerToken));
        assertPermitted(setProblems(supervisorAToken));
        assertForbidden(setProblems(supervisorBToken));
        assertForbidden(setProblems(contestantToken));
        assertForbidden(setProblems(userToken));
    }

    @Test
    void get_problems() {
        assertPermitted(getProblems(adminToken));
        assertPermitted(getProblems(managerToken));
        assertPermitted(getProblems(supervisorAToken));
        assertForbidden(getProblems(supervisorBToken));
        assertForbidden(getProblems(contestantToken));
        assertForbidden(getProblems(userToken));
        assertForbidden(getProblems(""));

        beginContest(contest);

        assertPermitted(getProblems(adminToken));
        assertPermitted(getProblems(managerToken));
        assertPermitted(getProblems(supervisorAToken));
        assertPermitted(getProblems(supervisorBToken));
        assertPermitted(getProblems(contestantToken));
        assertForbidden(getProblems(userToken));
        assertForbidden(getProblems(""));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(userToken));
        assertPermitted(getProblems(""));

        disableModule(contest, REGISTRATION);

        endContest(contest);

        assertPermitted(getProblems(adminToken));
        assertPermitted(getProblems(managerToken));
        assertPermitted(getProblems(supervisorAToken));
        assertPermitted(getProblems(supervisorBToken));
        assertPermitted(getProblems(contestantToken));
        assertForbidden(getProblems(userToken));
        assertForbidden(getProblems(""));

        enableModule(contest, REGISTRATION);

        assertPermitted(getProblems(userToken));
        assertPermitted(getProblems(""));
    }

    @Test
    void get_problems_virtual() {
        enableModule(contest, VIRTUAL, new ContestModulesConfig.Builder()
                .virtual(new VirtualModuleConfig.Builder().virtualDuration(Duration.ofHours(2)).build())
                .build());

        assertForbidden(getProblems(contestantToken));

        beginContest(contest);
        assertForbidden(getProblems(contestantToken));

        contestClient.startVirtualContest(contestantToken, contest.getJid());
        assertPermitted(getProblems(contestantToken));

        contestClient.resetVirtualContest(managerToken, contest.getJid());
        assertForbidden(getProblems(contestantToken));

        endContest(contest);
        assertPermitted(getProblems(contestantToken));
    }

    private ThrowingCallable setProblems(String token) {
        return () -> problemClient.setProblems(token, contest.getJid(), Collections.emptyList());
    }

    private ThrowingCallable getProblems(String token) {
        return callAll(
                () -> problemClient.getProblems(token, contest.getJid()),
                () -> problemClient.getProgrammingProblemWorksheet(token, contest.getJid(), "A"));
    }
}
