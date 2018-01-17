package judgels.jophiel.role;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.hibernate.AdminRoleHibernateDao;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class})
class RoleStoreIntegrationTests {
    private RoleStore store;

    @BeforeEach void before(SessionFactory sessionFactory) {
        AdminRoleDao adminRoleDao =
                new AdminRoleHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new RoleStore(adminRoleDao);
    }

    @Test void test_roles() {
        store.setSuperadmin("jidX");
        store.addAdmin("jid1");
        store.addAdmin("jid2");

        assertThat(store.isAdmin("jidX")).isTrue();
        assertThat(store.isAdmin("jid1")).isTrue();
        assertThat(store.isAdmin("jid2")).isTrue();
        assertThat(store.isAdmin("jid3")).isFalse();
    }
}
