package judgels.uriel.api.contest.contestant;

import static judgels.uriel.api.contest.module.ContestModuleType.DIVISION;
import static judgels.uriel.api.contest.module.ContestModuleType.REGISTRATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_A;
import static judgels.uriel.api.mocks.MockJophiel.USER_A_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_B_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.DivisionModuleConfig;
import judgels.uriel.api.contest.supervisor.SupervisorManagementPermission;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestContestantServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
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
        assertPermitted(upsertDeleteContestants(ADMIN_HEADER));
        assertPermitted(upsertDeleteContestants(MANAGER_HEADER));
        assertPermitted(upsertDeleteContestants(SUPERVISOR_A_HEADER));
        assertForbidden(upsertDeleteContestants(SUPERVISOR_B_HEADER));
        assertForbidden(upsertDeleteContestants(CONTESTANT_HEADER));
        assertForbidden(upsertDeleteContestants(USER_HEADER));
    }

    @Test
    void get_contestants() {
        assertPermitted(getContestants(ADMIN_HEADER));
        assertPermitted(getContestants(MANAGER_HEADER));
        assertPermitted(getContestants(SUPERVISOR_A_HEADER));
        assertPermitted(getContestants(SUPERVISOR_B_HEADER));
        assertForbidden(getContestants(CONTESTANT_HEADER));
        assertForbidden(getContestants(USER_HEADER));
    }

    @Test
    void get_approved_contestants() {
        assertPermitted(getApprovedContestants(ADMIN_HEADER));
        assertPermitted(getApprovedContestants(MANAGER_HEADER));
        assertPermitted(getApprovedContestants(SUPERVISOR_A_HEADER));
        assertPermitted(getApprovedContestants(SUPERVISOR_B_HEADER));
        assertPermitted(getApprovedContestants(CONTESTANT_HEADER));
        assertForbidden(getApprovedContestants(USER_HEADER));

        enableModule(contest, REGISTRATION);
        assertPermitted(getApprovedContestants(USER_HEADER));
    }

    @Test
    void register_myself_as_contestant() {
        assertForbidden(registerMyselfAsContestant(ADMIN_HEADER));
        assertForbidden(registerMyselfAsContestant(MANAGER_HEADER));
        assertForbidden(registerMyselfAsContestant(SUPERVISOR_A_HEADER));
        assertForbidden(registerMyselfAsContestant(SUPERVISOR_B_HEADER));
        assertForbidden(registerMyselfAsContestant(CONTESTANT_HEADER));
        assertForbidden(registerMyselfAsContestant(USER_HEADER));

        enableModule(contest, REGISTRATION);

        assertForbidden(registerMyselfAsContestant(ADMIN_HEADER));
        assertForbidden(registerMyselfAsContestant(MANAGER_HEADER));
        assertForbidden(registerMyselfAsContestant(SUPERVISOR_A_HEADER));
        assertForbidden(registerMyselfAsContestant(SUPERVISOR_B_HEADER));
        assertForbidden(registerMyselfAsContestant(CONTESTANT_HEADER));
        assertPermitted(registerMyselfAsContestant(USER_HEADER));
        contestantService.deleteContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER));

        enableModule(contest, DIVISION, new ContestModulesConfig.Builder()
                .division(new DivisionModuleConfig.Builder().division(1).build())
                .build());

        assertForbidden(registerMyselfAsContestant(USER_HEADER));
        assertPermitted(registerMyselfAsContestant(USER_A_HEADER));
        assertForbidden(registerMyselfAsContestant(USER_B_HEADER));
        contestantService.deleteContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER_A));

        disableModule(contest, DIVISION);

        beginContest(contest);
        assertPermitted(registerMyselfAsContestant(USER_HEADER));
        contestantService.deleteContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(USER));

        endContest(contest);
        assertForbidden(registerMyselfAsContestant(USER_HEADER));
    }

    @Test
    void unregister_myself_as_contestant() {
        assertForbidden(unregisterMyselfAsContestant(ADMIN_HEADER));
        assertForbidden(unregisterMyselfAsContestant(MANAGER_HEADER));
        assertForbidden(unregisterMyselfAsContestant(SUPERVISOR_A_HEADER));
        assertForbidden(unregisterMyselfAsContestant(SUPERVISOR_B_HEADER));
        assertForbidden(unregisterMyselfAsContestant(CONTESTANT_HEADER));
        assertForbidden(unregisterMyselfAsContestant(USER_HEADER));

        enableModule(contest, REGISTRATION);

        assertForbidden(unregisterMyselfAsContestant(ADMIN_HEADER));
        assertForbidden(unregisterMyselfAsContestant(MANAGER_HEADER));
        assertForbidden(unregisterMyselfAsContestant(SUPERVISOR_A_HEADER));
        assertForbidden(unregisterMyselfAsContestant(SUPERVISOR_B_HEADER));
        assertPermitted(unregisterMyselfAsContestant(CONTESTANT_HEADER));
        assertForbidden(unregisterMyselfAsContestant(USER_HEADER));
        contestantService.upsertContestants(SUPERVISOR_A_HEADER, contest.getJid(), ImmutableSet.of(CONTESTANT));

        beginContest(contest);
        assertForbidden(unregisterMyselfAsContestant(CONTESTANT_HEADER));

        endContest(contest);
        assertForbidden(unregisterMyselfAsContestant(CONTESTANT_HEADER));
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
