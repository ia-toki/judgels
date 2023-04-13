package judgels.uriel.api.contest.supervisor;

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

class ContestSupervisorServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private Contest contest;

    @BeforeEach
    void before() {
        contest = createContestWithRoles();
    }

    @Test
    void upsert_delete_supervisors() {
        assertPermitted(upsertDeleteSupervisors(ADMIN_HEADER));
        assertPermitted(upsertDeleteSupervisors(MANAGER_HEADER));
        assertForbidden(upsertDeleteSupervisors(SUPERVISOR_HEADER));
        assertForbidden(upsertDeleteSupervisors(CONTESTANT_HEADER));
        assertForbidden(upsertDeleteSupervisors(USER_HEADER));
    }

    @Test
    void get_supervisors() {
        assertPermitted(getSupervisors(ADMIN_HEADER));
        assertPermitted(getSupervisors(MANAGER_HEADER));
        assertPermitted(getSupervisors(SUPERVISOR_HEADER));
        assertForbidden(getSupervisors(CONTESTANT_HEADER));
        assertForbidden(getSupervisors(USER_HEADER));
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
