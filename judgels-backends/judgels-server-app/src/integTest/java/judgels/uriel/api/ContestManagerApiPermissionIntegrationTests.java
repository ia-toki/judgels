package judgels.uriel.api;

import java.util.Set;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_managers() {
        assertPermitted(upsertDeleteManagers(adminToken));
        assertForbidden(upsertDeleteManagers(managerToken));
        assertForbidden(upsertDeleteManagers(supervisorToken));
        assertForbidden(upsertDeleteManagers(contestantToken));
        assertForbidden(upsertDeleteManagers(userToken));
    }

    @Test
    void get_managers() {
        assertPermitted(getManagers(adminToken));
        assertPermitted(getManagers(managerToken));
        assertForbidden(getManagers(supervisorToken));
        assertForbidden(getManagers(contestantToken));
        assertForbidden(getManagers(userToken));
    }

    private ThrowingCallable upsertDeleteManagers(String token) {
        return callAll(
                () -> managerClient.upsertManagers(token, contest.getJid(), Set.of(USER)),
                () -> managerClient.deleteManagers(token, contest.getJid(), Set.of(USER)));
    }

    private ThrowingCallable getManagers(String token) {
        return () -> managerClient.getManagers(token, contest.getJid());
    }
}
