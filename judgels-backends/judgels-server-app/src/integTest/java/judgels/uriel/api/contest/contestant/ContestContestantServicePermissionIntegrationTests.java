package judgels.uriel.api.contest.contestant;

import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        assertPermitted(upsertDeleteContestants(adminHeader));
        assertPermitted(upsertDeleteContestants(managerHeader));
        assertPermitted(upsertDeleteContestants(supervisorAHeader));
        assertForbidden(upsertDeleteContestants(supervisorBHeader));
        assertForbidden(upsertDeleteContestants(contestantHeader));
        assertForbidden(upsertDeleteContestants(userHeader));
    }

    @Test
    void get_contestants() {
        assertPermitted(getContestants(adminHeader));
        assertPermitted(getContestants(managerHeader));
        assertPermitted(getContestants(supervisorAHeader));
        assertPermitted(getContestants(supervisorBHeader));
        assertForbidden(getContestants(contestantHeader));
        assertForbidden(getContestants(userHeader));
    }

    @Test
    void get_approved_contestants() {
        assertPermitted(getApprovedContestants(adminHeader));
        assertPermitted(getApprovedContestants(managerHeader));
        assertPermitted(getApprovedContestants(supervisorAHeader));
        assertPermitted(getApprovedContestants(supervisorBHeader));
        assertPermitted(getApprovedContestants(contestantHeader));
        assertForbidden(getApprovedContestants(userHeader));

        enableModule(contest, REGISTRATION);
        assertPermitted(getApprovedContestants(userHeader));
    }

    @Test
    void register_myself_as_contestant() {
        assertForbidden(registerMyselfAsContestant(adminHeader));
        assertForbidden(registerMyselfAsContestant(managerHeader));
        assertForbidden(registerMyselfAsContestant(supervisorAHeader));
        assertForbidden(registerMyselfAsContestant(supervisorBHeader));
        assertForbidden(registerMyselfAsContestant(contestantHeader));
        assertForbidden(registerMyselfAsContestant(userHeader));

        enableModule(contest, REGISTRATION);

        assertForbidden(registerMyselfAsContestant(adminHeader));
        assertForbidden(registerMyselfAsContestant(managerHeader));
        assertForbidden(registerMyselfAsContestant(supervisorAHeader));
        assertForbidden(registerMyselfAsContestant(supervisorBHeader));
        assertForbidden(registerMyselfAsContestant(contestantHeader));
        assertPermitted(registerMyselfAsContestant(userHeader));
        contestantService.deleteContestants(supervisorAHeader, contest.getJid(), ImmutableSet.of(USER));

        enableModule(contest, DIVISION, new ContestModulesConfig.Builder()
                .division(new DivisionModuleConfig.Builder().division(1).build())
                .build());

        assertForbidden(registerMyselfAsContestant(userHeader));
        assertPermitted(registerMyselfAsContestant(userAHeader));
        assertForbidden(registerMyselfAsContestant(userBHeader));
        contestantService.deleteContestants(supervisorAHeader, contest.getJid(), ImmutableSet.of(USER_A));

        disableModule(contest, DIVISION);

        beginContest(contest);
        assertPermitted(registerMyselfAsContestant(userHeader));
        contestantService.deleteContestants(supervisorAHeader, contest.getJid(), ImmutableSet.of(USER));

        endContest(contest);
        assertForbidden(registerMyselfAsContestant(userHeader));
    }

    @Test
    void unregister_myself_as_contestant() {
        assertForbidden(unregisterMyselfAsContestant(adminHeader));
        assertForbidden(unregisterMyselfAsContestant(managerHeader));
        assertForbidden(unregisterMyselfAsContestant(supervisorAHeader));
        assertForbidden(unregisterMyselfAsContestant(supervisorBHeader));
        assertForbidden(unregisterMyselfAsContestant(contestantHeader));
        assertForbidden(unregisterMyselfAsContestant(userHeader));

        enableModule(contest, REGISTRATION);

        assertForbidden(unregisterMyselfAsContestant(adminHeader));
        assertForbidden(unregisterMyselfAsContestant(managerHeader));
        assertForbidden(unregisterMyselfAsContestant(supervisorAHeader));
        assertForbidden(unregisterMyselfAsContestant(supervisorBHeader));
        assertPermitted(unregisterMyselfAsContestant(contestantHeader));
        assertForbidden(unregisterMyselfAsContestant(userHeader));
        contestantService.upsertContestants(supervisorAHeader, contest.getJid(), ImmutableSet.of(CONTESTANT));

        beginContest(contest);
        assertForbidden(unregisterMyselfAsContestant(contestantHeader));

        endContest(contest);
        assertForbidden(unregisterMyselfAsContestant(contestantHeader));
    }

    private ThrowingCallable upsertDeleteContestants(AuthHeader authHeader) {
        return callAll(
                () -> contestantService.upsertContestants(authHeader, contest.getJid(), ImmutableSet.of(USER)),
                () -> contestantService.deleteContestants(authHeader, contest.getJid(), ImmutableSet.of(USER)));
    }

    private ThrowingCallable getContestants(AuthHeader authHeader) {
        return () -> contestantService.getContestants(authHeader, contest.getJid(), Optional.empty());
    }

    private ThrowingCallable getApprovedContestants(AuthHeader authHeader) {
        return callAll(
                () -> contestantService.getApprovedContestants(authHeader, contest.getJid()),
                () -> contestantService.getApprovedContestantsCount(authHeader, contest.getJid()));
    }

    private ThrowingCallable registerMyselfAsContestant(AuthHeader authHeader) {
        return () -> contestantService.registerMyselfAsContestant(authHeader, contest.getJid());
    }

    private ThrowingCallable unregisterMyselfAsContestant(AuthHeader authHeader) {
        return () -> contestantService.unregisterMyselfAsContestant(authHeader, contest.getJid());
    }
}
