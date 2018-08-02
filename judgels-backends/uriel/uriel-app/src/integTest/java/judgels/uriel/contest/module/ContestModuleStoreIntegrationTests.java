package judgels.uriel.contest.module;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.module.FrozenScoreboardModuleConfig;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestModuleModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestModuleModel.class})
class ContestModuleStoreIntegrationTests {
    private ContestStore contestStore;
    private ContestModuleStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestModuleStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestData.Builder().slug("contest-b").build());

        FrozenScoreboardModuleConfig config = new FrozenScoreboardModuleConfig.Builder()
                .isOfficialScoreboardAllowed(false)
                .scoreboardFreezeTime(Instant.ofEpochSecond(42))
                .build();

        store.upsertFrozenScoreboardModule(contest.getJid(), config);

        assertThat(store.getFrozenScoreboardModuleConfig(contest.getJid())).contains(config);
    }
}
