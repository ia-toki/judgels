package judgels.jophiel.role;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.DaggerJophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestHibernateModule;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.persistence.AdminRoleModel;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class})
class RoleStoreIntegrationTests {
    private RoleStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = DaggerJophielIntegrationTestComponent.builder()
                .jophielIntegrationTestHibernateModule(new JophielIntegrationTestHibernateModule(sessionFactory))
                .build();
        store = component.roleStore();
    }

    @Test
    void test_roles() {
        store.setSuperadmin("jidX");
        store.addAdmin("jid1");
        store.addAdmin("jid2");

        assertThat(store.isAdmin("jidX")).isTrue();
        assertThat(store.getUserRole("jidX")).isEqualTo(Role.SUPERADMIN);
        assertThat(store.isAdmin("jid1")).isTrue();
        assertThat(store.getUserRole("jid1")).isEqualTo(Role.ADMIN);
        assertThat(store.isAdmin("jid2")).isTrue();
        assertThat(store.getUserRole("jid2")).isEqualTo(Role.ADMIN);
        assertThat(store.isAdmin("jid3")).isFalse();
        assertThat(store.getUserRole("jid3")).isEqualTo(Role.USER);
    }
}
