package judgels.uriel.contest.style;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.gabriel.api.LanguageRestriction;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestStyleModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestStyleModel.class})
class ContestStyleStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestStore contestStore;
    private ContestStyleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestStyleStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest-a").build());
        contestStore.createContest(new ContestCreateData.Builder().slug("contest-b").build());

        IoiContestStyleConfig config = new IoiContestStyleConfig.Builder()
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .usingLastAffectingPenalty(true)
                .build();

        store.upsertConfig(contest.getJid(), config);

        assertThat(store.getIoiStyleConfig(contest.getJid())).isEqualTo(config);
    }
}
