package judgels.api;

import static judgels.api.contest.module.ContestModuleType.HIDDEN;
import static judgels.api.contest.module.ContestModuleType.REGISTRATION;

import judgels.api.contest.Contest;
import judgels.contest.ContestWebClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class ContestWebApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private final ContestWebClient webClient = createClient(ContestWebClient.class);

    private Contest contest;

    @Test
    void get_web_config() {
        contest = createContestWithRoles();

        assertPermitted(getWebConfig(adminToken));
        assertPermitted(getWebConfig(managerToken));
        assertPermitted(getWebConfig(supervisorToken));
        assertPermitted(getWebConfig(contestantToken));
        assertForbidden(getWebConfig(userToken));
        assertForbidden(getWebConfig(""));

        enableModule(contest, REGISTRATION);

        assertPermitted(getWebConfig(adminToken));
        assertPermitted(getWebConfig(managerToken));
        assertPermitted(getWebConfig(supervisorToken));
        assertPermitted(getWebConfig(contestantToken));
        assertPermitted(getWebConfig(userToken));
        assertPermitted(getWebConfig(""));

        enableModule(contest, HIDDEN);

        assertPermitted(getWebConfig(adminToken));
        assertPermitted(getWebConfig(managerToken));
        assertForbidden(getWebConfig(supervisorToken));
        assertForbidden(getWebConfig(contestantToken));
        assertForbidden(getWebConfig(userToken));
        assertForbidden(getWebConfig(""));
    }

    private ThrowingCallable getWebConfig(String token) {
        return () -> webClient.getWebConfig(token, contest.getJid());
    }
}
