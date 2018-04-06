package judgels.uriel.contest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.ContestData;
import judgels.uriel.api.contest.ContestStyle;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import judgels.uriel.hibernate.ContestHibernateDao;
import judgels.uriel.hibernate.ContestManagerHibernateDao;
import judgels.uriel.hibernate.ContestRoleHibernateDao;
import judgels.uriel.hibernate.ContestSupervisorHibernateDao;
import judgels.uriel.persistence.AdminRoleDao;
import judgels.uriel.persistence.AdminRoleModel;
import judgels.uriel.persistence.ContestContestantDao;
import judgels.uriel.persistence.ContestContestantModel;
import judgels.uriel.persistence.ContestDao;
import judgels.uriel.persistence.ContestManagerDao;
import judgels.uriel.persistence.ContestManagerModel;
import judgels.uriel.persistence.ContestModel;
import judgels.uriel.persistence.ContestRoleDao;
import judgels.uriel.persistence.ContestSupervisorDao;
import judgels.uriel.persistence.ContestSupervisorModel;
import judgels.uriel.role.RoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
class ContestStoreIntegrationTests {
    private static final String ADMIN = "adminJid";
    private static final String USER_1 = "user1Jid";
    private static final String USER_2 = "user2Jid";
    private static final String USER_3 = "user3Jid";

    private ContestStore store;
    private RoleStore roleStore;

    private ContestContestantDao contestantDao;
    private ContestSupervisorDao supervisorDao;
    private ContestManagerDao managerDao;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        ContestDao contestDao = new ContestHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ContestRoleDao contestRoleDao = new ContestRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        contestantDao = new ContestContestantHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        supervisorDao = new ContestSupervisorHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        managerDao = new ContestManagerHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());

        roleStore = new RoleStore(adminRoleDao, contestRoleDao);
        store = new ContestStore(roleStore, contestDao);
    }

    @Test
    void get_contests() {
        Contest contestA = store.createContest(new ContestData.Builder()
                .name("contestA")
                .description("Contest A")
                .style(ContestStyle.IOI)
                .build());

        Contest contestB = store.createContest(new ContestData.Builder()
                .name("contestB")
                .description("Contest B")
                .style(ContestStyle.ICPC)
                .build());

        Contest contestC = store.createContest(new ContestData.Builder()
                .name("contestC")
                .description("Contest C")
                .style(ContestStyle.ICPC)
                .build());

        roleStore.addAdmin(ADMIN);
        addContestant(contestA.getJid(), USER_1);
        addContestant(contestA.getJid(), USER_2);
        addContestant(contestA.getJid(), USER_3);
        addSupervisor(contestB.getJid(), USER_2);
        addManager(contestC.getJid(), USER_3);

        assertThat(getContests(ADMIN)).containsExactly(contestA, contestB, contestC);
        assertThat(getContests(USER_1)).containsExactly(contestA);
        assertThat(getContests(USER_2)).containsExactly(contestA, contestB);
        assertThat(getContests(USER_3)).containsExactly(contestA, contestC);
    }

    private void addContestant(String contestJid, String userJid) {
        ContestContestantModel model = new ContestContestantModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        contestantDao.insert(model);
    }

    private void addSupervisor(String contestJid, String userJid) {
        ContestSupervisorModel model = new ContestSupervisorModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        supervisorDao.insert(model);
    }

    private void addManager(String contestJid, String userJid) {
        ContestManagerModel model = new ContestManagerModel();
        model.contestJid = contestJid;
        model.userJid = userJid;
        managerDao.insert(model);
    }

    private List<Contest> getContests(String userJid) {
        return store.getContests(userJid, SelectionOptions.DEFAULT).getData();
    }
}
