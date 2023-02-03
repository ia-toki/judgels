package judgels.uriel.api.contest.manager;

import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_managers() {
        assertPermitted(upsertDeleteManagers(ADMIN_HEADER));
        assertForbidden(upsertDeleteManagers(MANAGER_HEADER));
        assertForbidden(upsertDeleteManagers(SUPERVISOR_HEADER));
        assertForbidden(upsertDeleteManagers(CONTESTANT_HEADER));
        assertForbidden(upsertDeleteManagers(USER_HEADER));
    }

    @Test
    void get_managers() {
        assertPermitted(getManagers(ADMIN_HEADER));
        assertPermitted(getManagers(MANAGER_HEADER));
        assertForbidden(getManagers(SUPERVISOR_HEADER));
        assertForbidden(getManagers(CONTESTANT_HEADER));
        assertForbidden(getManagers(USER_HEADER));
    }

    private ThrowingCallable upsertDeleteManagers(AuthHeader authHeader) {
        return callAll(
                () -> managerService.upsertManagers(authHeader, contest.getJid(), ImmutableSet.of(USER)),
                () -> managerService.deleteManagers(authHeader, contest.getJid(), ImmutableSet.of(USER)));
    }

    private ThrowingCallable getManagers(AuthHeader authHeader) {
        return () -> managerService.getManagers(authHeader, contest.getJid(), Optional.empty());
    }
}
