package judgels.uriel.contest.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.contest.AbstractContestRoleCheckerIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContestEditorialRoleCheckerIntegrationTests extends AbstractContestRoleCheckerIntegrationTests {
    private ContestEditorialRoleChecker checker;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        prepare(sessionFactory);
        checker = component.contestEditorialRoleChecker();
    }

    @Test
    void view() {
        assertThat(checker.canView(contestB)).isFalse();
        assertThat(checker.canView(contestBStarted)).isFalse();
        assertThat(checker.canView(contestBFinished)).isFalse();

        moduleStore.upsertEditorialModule(contestB.getJid(), EditorialModuleConfig.DEFAULT);
        moduleStore.upsertEditorialModule(contestBStarted.getJid(), EditorialModuleConfig.DEFAULT);
        moduleStore.upsertEditorialModule(contestBFinished.getJid(), EditorialModuleConfig.DEFAULT);

        assertThat(checker.canView(contestB)).isFalse();
        assertThat(checker.canView(contestBStarted)).isFalse();
        assertThat(checker.canView(contestBFinished)).isTrue();
    }
}
