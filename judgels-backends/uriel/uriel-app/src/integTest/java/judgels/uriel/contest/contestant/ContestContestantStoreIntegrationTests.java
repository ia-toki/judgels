package judgels.uriel.contest.contestant;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.DaggerUrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.UrielIntegrationTestHibernateModule;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestContestantModel.class})
class ContestContestantStoreIntegrationTests {
    private ContestContestantStore store;
    private ContestStore contestStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = DaggerUrielIntegrationTestComponent.builder()
                .urielIntegrationTestHibernateModule(new UrielIntegrationTestHibernateModule(sessionFactory))
                .build();

        contestStore = component.contestStore();
        store = component.contestContestantStore();
    }

    @Test
    void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest").build());

        store.upsertContestant(contest.getJid(), "A");
        store.upsertContestant(contest.getJid(), "B");

        Set<String> contestantJids = store.getContestants(contest.getJid());
        assertThat(contestantJids).containsOnly("A", "B");
    }
}
