package judgels.uriel.api.contest.supervisor;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestSupervisorServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        assertPermitted(upsertDeleteSupervisors(adminHeader));
        assertPermitted(upsertDeleteSupervisors(managerHeader));
        assertForbidden(upsertDeleteSupervisors(supervisorHeader));
        assertForbidden(upsertDeleteSupervisors(contestantHeader));
        assertForbidden(upsertDeleteSupervisors(userHeader));
    }

    @Test
    void get_supervisors() {
        assertPermitted(getSupervisors(adminHeader));
        assertPermitted(getSupervisors(managerHeader));
        assertPermitted(getSupervisors(supervisorHeader));
        assertForbidden(getSupervisors(contestantHeader));
        assertForbidden(getSupervisors(userHeader));
    }

    private ThrowingCallable upsertDeleteSupervisors(AuthHeader authHeader) {
        return callAll(
                () -> supervisorService
                        .upsertSupervisors(authHeader, contest.getJid(), new ContestSupervisorUpsertData.Builder()
                                .addUsernames("USER")
                                .build()),
                () -> supervisorService.deleteSupervisors(authHeader, contest.getJid(), ImmutableSet.of(USER)));
    }

    private ThrowingCallable getSupervisors(AuthHeader authHeader) {
        return () -> supervisorService.getSupervisors(authHeader, contest.getJid(), Optional.empty());
    }
}
