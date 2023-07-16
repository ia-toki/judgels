package judgels.uriel.api;

import java.util.Set;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        assertPermitted(upsertDeleteSupervisors(adminToken));
        assertPermitted(upsertDeleteSupervisors(managerToken));
        assertForbidden(upsertDeleteSupervisors(supervisorToken));
        assertForbidden(upsertDeleteSupervisors(contestantToken));
        assertForbidden(upsertDeleteSupervisors(userToken));
    }

    @Test
    void get_supervisors() {
        assertPermitted(getSupervisors(adminToken));
        assertPermitted(getSupervisors(managerToken));
        assertPermitted(getSupervisors(supervisorToken));
        assertForbidden(getSupervisors(contestantToken));
        assertForbidden(getSupervisors(userToken));
    }

    private ThrowingCallable upsertDeleteSupervisors(String token) {
        return callAll(
                () -> supervisorClient.upsertSupervisors(token, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                        .addUsernames("USER")
                        .build()),
                () -> supervisorClient.deleteSupervisors(token, contest.getJid(), Set.of(USER)));
    }

    private ThrowingCallable getSupervisors(String token) {
        return () -> supervisorClient.getSupervisors(token, contest.getJid());
    }
}
