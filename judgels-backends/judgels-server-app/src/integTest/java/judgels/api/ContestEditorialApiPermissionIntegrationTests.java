package judgels.api;

import static judgels.api.contest.module.ContestModuleType.EDITORIAL;

import judgels.api.contest.Contest;
import judgels.api.contest.module.ContestModulesConfig;
import judgels.api.contest.module.EditorialModuleConfig;
import judgels.contest.ContestEditorialClient;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestEditorialApiPermissionIntegrationTests extends BaseContestApiIntegrationTests {
    private final ContestEditorialClient editorialClient = createClient(ContestEditorialClient.class);

    private Contest contest;

    @BeforeEach
    void before() {
        contest = buildContest()
                .begun()
                .problems("A", PROBLEM_1_SLUG)
                .build();
    }

    @Test
    void get_editorial() {
        assertForbidden(getEditorial());
        endContest(contest);
        assertForbidden(getEditorial());
        enableModule(contest, EDITORIAL, new ContestModulesConfig.Builder()
                .editorial(EditorialModuleConfig.DEFAULT)
                .build());
        assertPermitted(getEditorial());
    }

    private ThrowingCallable getEditorial() {
        return () -> editorialClient.getEditorial(null, contest.getJid());
    }
}
