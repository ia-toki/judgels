package judgels.jophiel.user.role;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.role.JophielRole;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.persistence.UserRoleModel;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.role.UserRoleStore;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserRoleModel.class})
class UserRoleStoreIntegrationTests extends AbstractIntegrationTests {
    private static final String SUPERADMIN = "superadminJid";
    private static final String ADMIN = "adminJid";
    private static final String USER = "userJid";

    private SuperadminRoleStore superadminRoleStore;
    private UserRoleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = createComponent(sessionFactory);

        superadminRoleStore = component.superadminRoleStore();
        store = component.userRoleStore();
    }

    @Test
    void test_roles() {
        superadminRoleStore.setSuperadmin(SUPERADMIN);
        store.upsertRole(ADMIN, new UserRole.Builder()
                .jophiel(JophielRole.ADMIN)
                .build());
        store.upsertRole(USER, new UserRole.Builder()
                .sandalphon("WRITER")
                .build());

        assertThat(store.getRole(SUPERADMIN)).isEqualTo(new UserRole.Builder()
                .jophiel(JophielRole.SUPERADMIN)
                .build());

        assertThat(store.getRole(ADMIN)).isEqualTo(new UserRole.Builder()
                .jophiel(JophielRole.ADMIN)
                .build());

        assertThat(store.getRole(USER)).isEqualTo(new UserRole.Builder()
                .jophiel(JophielRole.USER)
                .sandalphon("WRITER")
                .build());
    }
}
