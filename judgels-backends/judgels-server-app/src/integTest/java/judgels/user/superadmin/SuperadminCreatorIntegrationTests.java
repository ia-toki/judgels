package judgels.user.superadmin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.api.user.User;
import judgels.persistence.UserModel;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.role.SuperadminRoleStore;
import judgels.user.BaseUserIntegrationTests;
import judgels.user.UserIntegrationTestComponent;
import judgels.user.UserStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
public class SuperadminCreatorIntegrationTests extends BaseUserIntegrationTests {
    private static final SuperadminCreatorConfiguration CONFIG = new SuperadminCreatorConfiguration.Builder()
            .initialPassword("superpass")
            .build();

    private UserStore userStore;
    private SuperadminRoleStore superadminRoleStore;
    private SuperadminCreator superadminCreator;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UserIntegrationTestComponent component = createComponent(sessionFactory);
        userStore = component.userStore();
        superadminRoleStore = component.superadminRoleStore();
        superadminCreator = new SuperadminCreator(userStore, superadminRoleStore, Optional.of(CONFIG));
    }

    @Test
    void ensure_superadmin_exists() {
        // initially, there is no superadmin
        assertThat(userStore.getUserByUsername("superadmin")).isEmpty();

        superadminCreator.ensureSuperadminExists();

        // now, superadmin exists
        User user = userStore.getUserByUsernameAndPassword("superadmin", "superpass").get();
        assertThat(user.getUsername()).isEqualTo("superadmin");
        assertThat(superadminRoleStore.isSuperadmin(user.getJid())).isTrue();

        superadminCreator.ensureSuperadminExists();

        // superadmin still exists
        user = userStore.getUserByUsernameAndPassword("superadmin", "superpass").get();
        assertThat(user.getUsername()).isEqualTo("superadmin");
        assertThat(superadminRoleStore.isSuperadmin(user.getJid())).isTrue();
    }
}
