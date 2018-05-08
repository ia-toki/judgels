package judgels.uriel.contest.style;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestStyleModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestStyleModel.class})
class ContestStyleStoreIntegrationTests {
    private ContestStore contestStore;
    private ContestStyleStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestStyleStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder().name("contestA").build());
        contestStore.createContest(new ContestData.Builder().name("contestB").build());

        IoiContestStyleConfig config = new IoiContestStyleConfig.Builder()
                .languageRestriction(LanguageRestriction.noRestriction())
                .usingLastAffectingPenalty(true)
                .build();

        store.upsertConfig(contest.getJid(), config);

        assertThat(store.getIoiStyleConfig(contest.getJid())).isEqualTo(config);
    }
}
