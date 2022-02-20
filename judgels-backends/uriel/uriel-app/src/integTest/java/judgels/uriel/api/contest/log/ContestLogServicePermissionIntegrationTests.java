package judgels.uriel.api.contest.log;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;
import static judgels.uriel.api.mocks.MockJophiel.ADMIN_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.CONTESTANT_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.MANAGER_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.SUPERVISOR_HEADER;
import static judgels.uriel.api.mocks.MockJophiel.USER_HEADER;
import static judgels.uriel.api.mocks.MockSandalphon.PROBLEM_1_SLUG;

import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.AbstractContestServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestLogServicePermissionIntegrationTests extends AbstractContestServiceIntegrationTests {
    private final ContestLogService logService = createService(ContestLogService.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContestWithRoles()
                .begun()
                .problems("A", PROBLEM_1_SLUG)
                .modules(CLARIFICATION)
                .build();
    }

    @Test
    void get_logs() {
        assertPermitted(getLogs(ADMIN_HEADER));
        assertPermitted(getLogs(MANAGER_HEADER));
        assertForbidden(getLogs(SUPERVISOR_HEADER));
        assertForbidden(getLogs(CONTESTANT_HEADER));
        assertForbidden(getLogs(USER_HEADER));
    }

    private ThrowingCallable getLogs(AuthHeader authHeader) {
        return () -> logService.getLogs(authHeader, contest.getJid(), empty(), empty(), empty());
    }
}
