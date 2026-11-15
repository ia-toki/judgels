package judgels.api;

import java.util.Set;
import judgels.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestManagerApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void add_remove_managers() {
        assertPermitted(addRemoveManagers(adminToken));
        assertForbidden(addRemoveManagers(managerToken));
        assertForbidden(addRemoveManagers(supervisorToken));
        assertForbidden(addRemoveManagers(contestantToken));
        assertForbidden(addRemoveManagers(userToken));
    }

    @Test
    void get_managers() {
        assertPermitted(getManagers(adminToken));
        assertPermitted(getManagers(managerToken));
        assertForbidden(getManagers(supervisorToken));
        assertForbidden(getManagers(contestantToken));
        assertForbidden(getManagers(userToken));
    }

    private ThrowingCallable addRemoveManagers(String token) {
        return callAll(
                () -> managerClient.addManagers(token, contest.getJid(), Set.of(USER)),
                () -> managerClient.removeManagers(token, contest.getJid(), Set.of(USER)));
    }

    private ThrowingCallable getManagers(String token) {
        return () -> managerClient.getManagers(token, contest.getJid());
    }
}
