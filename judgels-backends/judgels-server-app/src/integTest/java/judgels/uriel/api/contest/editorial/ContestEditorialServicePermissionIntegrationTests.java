package judgels.uriel.api.contest.editorial;

import static java.util.Optional.empty;
import static judgels.uriel.api.contest.module.ContestModuleType.EDITORIAL;

import judgels.uriel.api.BaseUrielServiceIntegrationTests;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.module.ContestModulesConfig;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestEditorialServicePermissionIntegrationTests extends BaseUrielServiceIntegrationTests {
    private final ContestEditorialService editorialService = createService(ContestEditorialService.class);

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
        return () -> editorialService.getEditorial(contest.getJid(), empty());
    }
}
