package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {ContestModel.class, ContestContestantModel.class})
class ContestStoreIntegrationTests {
    private ContestStore store;
    private ContestContestantStore contestantStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        store = new ContestStore(contestDao);
        contestantStore = new ContestContestantStore(contestantDao);
    }

    @Test
    void get_contests() {
        Contest contestA = store.createContest(new ContestData.Builder()
                .name("Turfa")
                .description("Ganteng")
                .style(ContestStyle.IOI)
                .build());

        Contest contestB = store.createContest(new ContestData.Builder()
                .name("Ganteng")
                .description("Turfa")
                .style(ContestStyle.ICPC)
                .build());

        String userJidA = "userJidA";
        String userJidB  = "userJidB";
        String userJidC = "userJidC";

        contestantStore.addContestants(contestA.getJid(), ImmutableList.of(userJidA, userJidB));
        contestantStore.addContestants(contestB.getJid(), ImmutableList.of(userJidB));

        Page<Contest> contestPageA = store.getContests(userJidA, 1, 10);
        assertThat(contestPageA.getTotalData()).isEqualTo(1);
        assertThat(contestPageA.getData()).containsExactly(contestA);

        Page<Contest> contestPageB = store.getContests(userJidB, 1, 10);
        assertThat(contestPageB.getTotalData()).isEqualTo(2);
        assertThat(contestPageB.getData()).containsExactly(contestA, contestB);

        Page<Contest> contestPageC = store.getContests(userJidC, 1, 10);
        assertThat(contestPageC.getTotalData()).isZero();
        assertThat(contestPageC.getData()).isEmpty();
    }
}
