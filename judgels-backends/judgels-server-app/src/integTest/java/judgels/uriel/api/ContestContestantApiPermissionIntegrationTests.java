package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import java.util.Set;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .supervisorWithManagementPermissions(SUPERVISOR_A, SupervisorManagementPermission.CONTESTANT)
                .supervisors(SUPERVISOR_B)
                .build();
    }

    @Test
    void upsert_delete_contestants() {
        assertPermitted(upsertDeleteContestants(adminToken));
        assertPermitted(upsertDeleteContestants(managerToken));
        assertPermitted(upsertDeleteContestants(supervisorAToken));
        assertForbidden(upsertDeleteContestants(supervisorBToken));
        assertForbidden(upsertDeleteContestants(contestantToken));
        assertForbidden(upsertDeleteContestants(userToken));
    }

    @Test
    void get_contestants() {
        assertPermitted(getContestants(adminToken));
        assertPermitted(getContestants(managerToken));
        assertPermitted(getContestants(supervisorAToken));
        assertPermitted(getContestants(supervisorBToken));
        assertForbidden(getContestants(contestantToken));
        assertForbidden(getContestants(userToken));
    }

    @Test
    void get_approved_contestants() {
        assertPermitted(getApprovedContestants(adminToken));
        assertPermitted(getApprovedContestants(managerToken));
        assertPermitted(getApprovedContestants(supervisorAToken));
        assertPermitted(getApprovedContestants(supervisorBToken));
        assertPermitted(getApprovedContestants(contestantToken));
        assertForbidden(getApprovedContestants(userToken));

        enableModule(contest, REGISTRATION);
        assertPermitted(getApprovedContestants(userToken));
    }

    @Test
    void register_myself_as_contestant() {
        assertForbidden(registerMyselfAsContestant(adminToken));
        assertForbidden(registerMyselfAsContestant(managerToken));
        assertForbidden(registerMyselfAsContestant(supervisorAToken));
        assertForbidden(registerMyselfAsContestant(supervisorBToken));
        assertForbidden(registerMyselfAsContestant(contestantToken));
        assertForbidden(registerMyselfAsContestant(userToken));

        enableModule(contest, REGISTRATION);

        assertForbidden(registerMyselfAsContestant(adminToken));
        assertForbidden(registerMyselfAsContestant(managerToken));
        assertForbidden(registerMyselfAsContestant(supervisorAToken));
        assertForbidden(registerMyselfAsContestant(supervisorBToken));
        assertForbidden(registerMyselfAsContestant(contestantToken));
        assertPermitted(registerMyselfAsContestant(userToken));
        contestantClient.deleteContestants(supervisorAToken, contest.getJid(), Set.of(USER));

        enableModule(contest, DIVISION, new ContestModulesConfig.Builder()
                .division(new DivisionModuleConfig.Builder().division(1).build())
                .build());

        assertForbidden(registerMyselfAsContestant(userToken));
        assertPermitted(registerMyselfAsContestant(userAToken));
        assertForbidden(registerMyselfAsContestant(userBToken));
        contestantClient.deleteContestants(supervisorAToken, contest.getJid(), Set.of(USER_A));

        disableModule(contest, DIVISION);

        beginContest(contest);
        assertPermitted(registerMyselfAsContestant(userToken));
        contestantClient.deleteContestants(supervisorAToken, contest.getJid(), Set.of(USER));

        endContest(contest);
        assertForbidden(registerMyselfAsContestant(userToken));
    }

    @Test
    void unregister_myself_as_contestant() {
        assertForbidden(unregisterMyselfAsContestant(adminToken));
        assertForbidden(unregisterMyselfAsContestant(managerToken));
        assertForbidden(unregisterMyselfAsContestant(supervisorAToken));
        assertForbidden(unregisterMyselfAsContestant(supervisorBToken));
        assertForbidden(unregisterMyselfAsContestant(contestantToken));
        assertForbidden(unregisterMyselfAsContestant(userToken));

        enableModule(contest, REGISTRATION);

        assertForbidden(unregisterMyselfAsContestant(adminToken));
        assertForbidden(unregisterMyselfAsContestant(managerToken));
        assertForbidden(unregisterMyselfAsContestant(supervisorAToken));
        assertForbidden(unregisterMyselfAsContestant(supervisorBToken));
        assertPermitted(unregisterMyselfAsContestant(contestantToken));
        assertForbidden(unregisterMyselfAsContestant(userToken));
        contestantClient.upsertContestants(supervisorAToken, contest.getJid(), Set.of(CONTESTANT));

        beginContest(contest);
        assertForbidden(unregisterMyselfAsContestant(contestantToken));

        endContest(contest);
        assertForbidden(unregisterMyselfAsContestant(contestantToken));
    }

    private ThrowingCallable upsertDeleteContestants(String token) {
        return callAll(
                () -> contestantClient.upsertContestants(token, contest.getJid(), Set.of(USER)),
                () -> contestantClient.deleteContestants(token, contest.getJid(), Set.of(USER)));
    }

    private ThrowingCallable getContestants(String token) {
        return () -> contestantClient.getContestants(token, contest.getJid());
    }

    private ThrowingCallable getApprovedContestants(String token) {
        return callAll(
                () -> contestantClient.getApprovedContestants(token, contest.getJid()),
                () -> contestantClient.getApprovedContestantsCount(token, contest.getJid()));
    }

    private ThrowingCallable registerMyselfAsContestant(String token) {
        return () -> contestantClient.registerMyselfAsContestant(token, contest.getJid());
    }

    private ThrowingCallable unregisterMyselfAsContestant(String token) {
        return () -> contestantClient.unregisterMyselfAsContestant(token, contest.getJid());
    }
}
