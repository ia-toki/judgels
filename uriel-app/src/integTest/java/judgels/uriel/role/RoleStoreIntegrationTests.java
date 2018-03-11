package judgels.uriel.role;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.contest.contestant.ContestContestantDao;
import judgels.uriel.contest.contestant.ContestContestantModel;
import judgels.uriel.hibernate.AdminRoleHibernateDao;
import judgels.uriel.hibernate.ContestContestantHibernateDao;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class, ContestContestantModel.class})
class RoleStoreIntegrationTests {
    private RoleStore store;
    private ContestContestantDao contestantDao;

    @BeforeEach void before(SessionFactory sessionFactory) {
        AdminRoleDao adminRoleDao =
                new AdminRoleHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        contestantDao = new ContestContestantHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new RoleStore(adminRoleDao, contestantDao);
    }

    @Test void test_roles() {
        store.addAdmin("jid1");

        assertThat(store.isAdmin("jid1")).isTrue();
        assertThat(store.isAdmin("jidX")).isFalse();

        ContestContestantModel contestantModel = new ContestContestantModel();
        contestantModel.userJid = "contestantJid";
        contestantModel.contestJid = "contestA";

        contestantDao.insert(contestantModel);

        assertThat(store.isContestant("contestA", "contestantJid")).isTrue();
    }
}
