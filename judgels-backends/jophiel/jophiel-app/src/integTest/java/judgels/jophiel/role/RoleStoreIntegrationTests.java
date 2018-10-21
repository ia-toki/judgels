package judgels.jophiel.role;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.role.Role;
import judgels.jophiel.persistence.AdminRoleModel;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class})
class RoleStoreIntegrationTests extends AbstractIntegrationTests {
    private static final String SUPERADMIN = "superadminJid";
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";

    private SuperadminRoleStore superadminRoleStore;
    private AdminRoleStore adminRoleStore;
    private RoleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = createComponent(sessionFactory);

        superadminRoleStore = component.superadminRoleStore();
        adminRoleStore = component.adminRoleStore();
        store = component.roleStore();
    }

    @Test
    void test_roles() {
        superadminRoleStore.setSuperadmin(SUPERADMIN);
        adminRoleStore.addAdmin(ADMIN);

        assertThat(store.getUserRole(SUPERADMIN)).isEqualTo(Role.SUPERADMIN);
        assertThat(store.getUserRole(ADMIN)).isEqualTo(Role.ADMIN);
        assertThat(store.getUserRole(USER)).isEqualTo(Role.USER);
    }
}
