package judgels.uriel.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
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
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {
        AdminRoleModel.class,
        ContestModel.class,
        ContestContestantModel.class,
        ContestSupervisorModel.class,
        ContestManagerModel.class})
class RoleStoreIntegrationTests {
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";
    private static final String CONTESTANT = "contestantJid";
    private static final String SUPERVISOR = "supervisorJid";
    private static final String MANAGER = "managerJid";

    private RoleStore store;
    private ContestDao contestDao;
    private ContestContestantDao contestantDao;
    private ContestSupervisorDao supervisorDao;
    private ContestManagerDao managerDao;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        AdminRoleDao adminRoleDao = new AdminRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        ContestRoleDao contestRoleDao = new ContestRoleHibernateDao(
                sessionFactory,
                new FixedClock(),
                new FixedActorProvider());

        store = new RoleStore(adminRoleDao, contestRoleDao);

        contestDao = new ContestHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        contestantDao = new ContestContestantHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        supervisorDao = new ContestSupervisorHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        managerDao = new ContestManagerHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider()) {};
    }

    @Test
    void roles() {
        store.addAdmin(ADMIN);

        assertThat(store.isAdmin(ADMIN)).isTrue();
        assertThat(store.isAdmin(USER)).isFalse();
    }

    @Test
    void contest_roles() {
        ContestModel contestModel1 = new ContestModel();
        contestModel1.name = "Contest A";
        contestModel1.description = "";
        contestModel1.style = "";
        contestModel1.beginTime = Instant.ofEpochSecond(42);
        contestModel1.duration = 42;
        contestModel1 = contestDao.insert(contestModel1);
        String contestJid1 = contestModel1.jid;

        ContestModel contestModel2 = new ContestModel();
        contestModel2.name = "Contest B";
        contestModel2.description = "";
        contestModel2.style = "";
        contestModel2.beginTime = Instant.ofEpochSecond(42);
        contestModel2.duration = 42;
        contestModel2 = contestDao.insert(contestModel2);
        String contestJid2 = contestModel2.jid;

        ContestContestantModel contestantModel = new ContestContestantModel();
        contestantModel.userJid = CONTESTANT;
        contestantModel.contestJid = contestJid1;
        contestantDao.insert(contestantModel);

        ContestSupervisorModel supervisorModel = new ContestSupervisorModel();
        supervisorModel.userJid = SUPERVISOR;
        supervisorModel.contestJid = contestJid1;
        supervisorDao.insert(supervisorModel);

        ContestManagerModel managerModel = new ContestManagerModel();
        managerModel.userJid = MANAGER;
        managerModel.contestJid = contestJid1;
        managerDao.insert(managerModel);

        assertThat(store.isContestContestantOrAbove(USER, contestJid1)).isFalse();
        assertThat(store.isContestSupervisorOrAbove(USER, contestJid1)).isFalse();
        assertThat(store.isContestManager(USER, contestJid1)).isFalse();

        assertThat(store.isContestContestantOrAbove(CONTESTANT, contestJid1)).isTrue();
        assertThat(store.isContestContestantOrAbove(CONTESTANT, contestJid2)).isFalse();
        assertThat(store.isContestSupervisorOrAbove(CONTESTANT, contestJid1)).isFalse();
        assertThat(store.isContestSupervisorOrAbove(CONTESTANT, contestJid2)).isFalse();
        assertThat(store.isContestManager(CONTESTANT, contestJid1)).isFalse();
        assertThat(store.isContestManager(CONTESTANT, contestJid2)).isFalse();

        assertThat(store.isContestContestantOrAbove(SUPERVISOR, contestJid1)).isTrue();
        assertThat(store.isContestContestantOrAbove(SUPERVISOR, contestJid2)).isFalse();
        assertThat(store.isContestSupervisorOrAbove(SUPERVISOR, contestJid1)).isTrue();
        assertThat(store.isContestSupervisorOrAbove(SUPERVISOR, contestJid2)).isFalse();
        assertThat(store.isContestManager(SUPERVISOR, contestJid1)).isFalse();
        assertThat(store.isContestManager(SUPERVISOR, contestJid2)).isFalse();

        assertThat(store.isContestContestantOrAbove(MANAGER, contestJid1)).isTrue();
        assertThat(store.isContestContestantOrAbove(MANAGER, contestJid2)).isFalse();
        assertThat(store.isContestSupervisorOrAbove(MANAGER, contestJid1)).isTrue();
        assertThat(store.isContestSupervisorOrAbove(MANAGER, contestJid2)).isFalse();
        assertThat(store.isContestManager(MANAGER, contestJid1)).isTrue();
        assertThat(store.isContestManager(MANAGER, contestJid2)).isFalse();
    }
}
