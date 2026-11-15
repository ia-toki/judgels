package judgels.api;

import java.util.Set;
import judgels.api.contest.Contest;
import judgels.api.contest.supervisor.ContestSupervisorsAddData;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void add_remove_supervisors() {
        assertPermitted(addRemoveSupervisors(adminToken));
        assertPermitted(addRemoveSupervisors(managerToken));
        assertForbidden(addRemoveSupervisors(supervisorToken));
        assertForbidden(addRemoveSupervisors(contestantToken));
        assertForbidden(addRemoveSupervisors(userToken));
    }

    @Test
    void get_supervisors() {
        assertPermitted(getSupervisors(adminToken));
        assertPermitted(getSupervisors(managerToken));
        assertPermitted(getSupervisors(supervisorToken));
        assertForbidden(getSupervisors(contestantToken));
        assertForbidden(getSupervisors(userToken));
    }

    private ThrowingCallable addRemoveSupervisors(String token) {
        return callAll(
                () -> supervisorClient.addSupervisors(token, contest.getJid(), new ContestSupervisorsAddData.Builder()
                        .addUsernames("USER")
                        .build()),
                () -> supervisorClient.removeSupervisors(token, contest.getJid(), Set.of(USER)));
    }

    private ThrowingCallable getSupervisors(String token) {
        return () -> supervisorClient.getSupervisors(token, contest.getJid());
    }
}
