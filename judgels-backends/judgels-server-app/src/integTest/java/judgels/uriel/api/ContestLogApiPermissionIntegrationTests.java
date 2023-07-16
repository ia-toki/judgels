package judgels.uriel.api;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION;

import judgels.uriel.ContestLogClient;
import judgels.uriel.api.contest.Contest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestLogApiPermissionIntegrationTests extends BaseUrielApiIntegrationTests {
    private final ContestLogClient logClient = createClient(ContestLogClient.class);

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
        assertPermitted(getLogs(adminToken));
        assertPermitted(getLogs(managerToken));
        assertForbidden(getLogs(supervisorToken));
        assertForbidden(getLogs(contestantToken));
        assertForbidden(getLogs(userToken));
    }

    private ThrowingCallable getLogs(String token) {
        return () -> logClient.getLogs(token, contest.getJid(), null);
    }
}
