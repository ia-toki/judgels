package judgels.uriel.contest.contestant;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.hibernate.ContestRawHibernateDao;
import judgels.uriel.hibernate.HibernateDaos.ContestContestantHibernateDao;
import judgels.uriel.hibernate.HibernateDaos.ContestHibernateDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRawDao;
import judgels.uriel.persistence.Daos.ContestContestantDao;
import judgels.uriel.persistence.Daos.ContestDao;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestContestantModel.class})
class ContestContestantStoreIntegrationTests {
    private ContestContestantStore store;
    private ContestStore contestStore;

    @BeforeEach void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        ContestRawDao contestRawDao = new ContestRawHibernateDao(sessionFactory);

        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        contestStore = new ContestStore(contestDao, contestRawDao);
        store = new ContestContestantStore(contestantDao);
    }

    @Test void can_do_basic_crud() {
        Contest contest = contestStore.createContest(new ContestData.Builder()
                .name("contestA")
                .description("contest A")
                .style(ContestStyle.IOI)
                .build());

        store.addContestants(contest.getJid(), ImmutableList.of("A", "B"));

        Page<String> contestantJids = store.getContestantJids(contest.getJid(), 1, 10);
        assertThat(contestantJids.getTotalData()).isEqualTo(2);
        assertThat(contestantJids.getData()).containsOnly("A", "B");
    }

    @Test void can_add_without_duplication() {
        Contest contest = contestStore.createContest(new ContestData.Builder()
                .name("contestA")
                .description("contest A")
                .style(ContestStyle.IOI)
                .build());

        store.addContestants(contest.getJid(), ImmutableList.of("A", "B"));
        store.addContestants(contest.getJid(), ImmutableList.of("A", "B", "B", "C", "D"));

        Page<String> contestantJids = store.getContestantJids(contest.getJid(), 1, 10);
        assertThat(contestantJids.getTotalData()).isEqualTo(4);
        assertThat(contestantJids.getData()).containsOnly("A", "B", "C", "D");
    }

    @Test void can_accept_empty_set() {
        Contest contest = contestStore.createContest(new ContestData.Builder()
                .name("contestA")
                .description("contest A")
                .style(ContestStyle.IOI)
                .build());

        store.addContestants(contest.getJid(), ImmutableList.of());

        Page<String> contestantJids = store.getContestantJids(contest.getJid(), 1, 10);
        assertThat(contestantJids.getTotalData()).isEqualTo(0);
    }
}
