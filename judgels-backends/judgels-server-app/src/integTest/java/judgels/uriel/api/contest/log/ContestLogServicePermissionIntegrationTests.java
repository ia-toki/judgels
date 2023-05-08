package judgels.uriel.api.contest.log;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;

import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestLogServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
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
        assertPermitted(getLogs(adminHeader));
        assertPermitted(getLogs(managerHeader));
        assertForbidden(getLogs(supervisorHeader));
        assertForbidden(getLogs(contestantHeader));
        assertForbidden(getLogs(userHeader));
    }

    private ThrowingCallable getLogs(AuthHeader authHeader) {
        return () -> logService.getLogs(authHeader, contest.getJid(), empty(), empty(), empty());
    }
}
