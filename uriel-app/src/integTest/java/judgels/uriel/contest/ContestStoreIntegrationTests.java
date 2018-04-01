package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.role.RoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class, ContestModel.class, ContestContestantModel.class})
class ContestStoreIntegrationTests {
    private ContestStore store;
    private RoleStore roleStore;
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

        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        roleStore = new RoleStore(adminRoleDao, contestantDao);
        contestantStore = new ContestContestantStore(contestantDao);
        store = new ContestStore(roleStore, contestDao);
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

        String adminJid = "adminJid";
        String userJidA = "userJidA";
        String userJidB  = "userJidB";
        String userJidC = "userJidC";

        contestantStore.addContestants(contestA.getJid(), ImmutableList.of(userJidA, userJidB));
        contestantStore.addContestants(contestB.getJid(), ImmutableList.of(userJidB));

        Page<Contest> contests = store.getContests(userJidA, SelectionOptions.DEFAULT);
        assertThat(contests.getData()).containsExactly(contestA);

        contests = store.getContests(userJidB, SelectionOptions.DEFAULT);
        assertThat(contests.getData()).containsExactly(contestA, contestB);

        contests = store.getContests(userJidC, SelectionOptions.DEFAULT);
        assertThat(contests.getData()).isEmpty();

        roleStore.addAdmin(adminJid);
        contests = store.getContests(adminJid, SelectionOptions.DEFAULT);
        assertThat(contests.getData()).containsExactly(contestA, contestB);
    }
}
