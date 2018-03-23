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
public class ContestStoreIntegrationTests {
    private ContestStore store;
    private ContestContestantStore contestContestantStore;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());
        ContestRawDao contestRawDao = new ContestRawHibernateDao(sessionFactory);

        ContestContestantDao contestantDao = new ContestContestantHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        store = new ContestStore(contestDao, contestRawDao);
        contestContestantStore = new ContestContestantStore(contestantDao);
    }

    @Test
    void test_contest() {
        Contest contestA = store.createContest(new ContestData.Builder()
                .name("Turfa")
                .description("Ganteng")
                .style(ContestStyle.IOI)
                .build()
        );

        Contest contestB = store.createContest(new ContestData.Builder()
                .name("Ganteng")
                .description("Turfa")
                .style(ContestStyle.ICPC)
                .build()
        );

        String userJidA = "userJidA";
        String userJidB  = "userJidB";
        String userJidC = "userJidC";

        contestContestantStore.addContestants(contestA.getJid(), ImmutableList.of(userJidA, userJidB));
        contestContestantStore.addContestants(contestB.getJid(), ImmutableList.of(userJidB));

        Page<Contest> contestPageA = store.getContests(userJidA, 1, 10);
        Page<Contest> contestPageB = store.getContests(userJidB, 1, 10);
        Page<Contest> contestPageC = store.getContests(userJidC, 1, 10);

        assertThat(contestPageA.getTotalData()).isEqualTo(1);
        assertThat(contestPageA.getData()).containsExactly(contestA);

        assertThat(contestPageB.getTotalData()).isEqualTo(2);
        assertThat(contestPageB.getData()).containsExactly(contestA, contestB);

        assertThat(contestPageC.getTotalData()).isEqualTo(0);
        assertThat(contestPageC.getData()).isEmpty();
    }
}
