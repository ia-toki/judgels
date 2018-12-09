package judgels.uriel.contest.manager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestCreateData;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestManagerModel.class})
class ContestManagerStoreIntegrationTests extends AbstractIntegrationTests {
    private ContestManagerStore store;
    private ContestStore contestStore;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        contestStore = component.contestStore();
        store = component.contestManagerStore();
    }

    @Test
    void crud_flow() {
        Contest contest = contestStore.createContest(new ContestCreateData.Builder().slug("contest").build());

        assertThat(store.upsertManager(contest.getJid(), "userJidA")).isTrue();
        assertThat(store.upsertManager(contest.getJid(), "userJidB")).isTrue();
        assertThat(store.upsertManager(contest.getJid(), "userJidA")).isFalse();

        Page<ContestManager> managers = store.getManagers(contest.getJid(), Optional.empty());
        assertThat(managers.getPage()).containsOnly(
                new ContestManager.Builder().userJid("userJidA").build(),
                new ContestManager.Builder().userJid("userJidB").build());

        assertThat(store.deleteManager(contest.getJid(), "userJidA")).isTrue();
        assertThat(store.deleteManager(contest.getJid(), "userJidC")).isFalse();

        managers = store.getManagers(contest.getJid(), Optional.empty());
        assertThat(managers.getPage()).containsOnly(
                new ContestManager.Builder().userJid("userJidB").build());
    }
}
