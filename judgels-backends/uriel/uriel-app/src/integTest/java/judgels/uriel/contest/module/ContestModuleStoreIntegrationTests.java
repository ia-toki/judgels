package judgels.uriel.contest.module;

import static judgels.uriel.api.contest.module.ContestModuleType.CLARIFICATION_TIME_LIMIT;
import static judgels.uriel.api.contest.module.ContestModuleType.FROZEN_SCOREBOARD;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.module.ClarificationTimeLimitModuleConfig;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestModuleModel.class})
class ContestModuleStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestModuleStore store;

    private Session currentSession;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        currentSession = sessionFactory.getCurrentSession();

        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestModuleStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        FrozenScoreboardModuleConfig config1 = new FrozenScoreboardModuleConfig.Builder()
                .isOfficialScoreboardAllowed(false)
                .freezeDurationBeforeEndTime(Duration.ofHours(1))
                .build();
        store.upsertFrozenScoreboardModule(contest.getJid(), config1);
        assertThat(store.getFrozenScoreboardModuleConfig(contest.getJid())).contains(config1);

        FrozenScoreboardModuleConfig config2 = new FrozenScoreboardModuleConfig.Builder()
                .isOfficialScoreboardAllowed(false)
                .freezeDurationBeforeEndTime(Duration.ofHours(1))
                .build();
        store.upsertFrozenScoreboardModule(contest.getJid(), config2);

        store.upsertPausedModule(contest.getJid());
        store.disablePausedModule(contest.getJid());
        store.upsertPausedModule(contest.getJid());
        store.disablePausedModule(contest.getJid());

        currentSession.flush();

        ClarificationTimeLimitModuleConfig config3 = new ClarificationTimeLimitModuleConfig.Builder()
                .clarificationDuration(Duration.ofHours(1))
                .build();
        store.upsertClarificationTimeLimitModule(contest.getJid(), config3);

        assertThat(store.getEnabledModules(contest.getJid()))
                .containsExactlyInAnyOrder(FROZEN_SCOREBOARD, CLARIFICATION_TIME_LIMIT);
    }
}
