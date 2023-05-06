package judgels.uriel.api.contest.manager;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_managers() {
        assertPermitted(upsertDeleteManagers(adminHeader));
        assertForbidden(upsertDeleteManagers(managerHeader));
        assertForbidden(upsertDeleteManagers(supervisorHeader));
        assertForbidden(upsertDeleteManagers(contestantHeader));
        assertForbidden(upsertDeleteManagers(userHeader));
    }

    @Test
    void get_managers() {
        assertPermitted(getManagers(adminHeader));
        assertPermitted(getManagers(managerHeader));
        assertForbidden(getManagers(supervisorHeader));
        assertForbidden(getManagers(contestantHeader));
        assertForbidden(getManagers(userHeader));
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
