package judgels.user.superadmin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.api.user.User;
import judgels.api.user.role.UserRole;
import judgels.persistence.WithHibernateSession;
import judgels.persistence.model.UserModel;
import judgels.persistence.model.UserRoleModel;
import judgels.user.BaseUserIntegrationTests;
import judgels.user.UserIntegrationTestComponent;
import judgels.user.UserStore;
import judgels.user.role.UserRoleStore;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class, UserRoleModel.class})
public class SuperadminCreatorIntegrationTests extends BaseUserIntegrationTests {
    private static final SuperadminCreatorConfiguration CONFIG = new SuperadminCreatorConfiguration.Builder()
            .initialPassword("superpass")
            .build();

    private UserStore userStore;
    private UserRoleStore userRoleStore;
    private SuperadminCreator superadminCreator;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UserIntegrationTestComponent component = createComponent(sessionFactory);
        userStore = component.userStore();
        userRoleStore = component.userRoleStore();
        superadminCreator = new SuperadminCreator(userStore, userRoleStore, Optional.of(CONFIG));
    }

    @Test
    void ensure_superadmin_exists() {
        // initially, there is no superadmin
        assertThat(userStore.getUserByUsername("superadmin")).isEmpty();

        superadminCreator.ensureSuperadminExists();

        // now, superadmin exists
        User user = userStore.getUserByUsernameAndPassword("superadmin", "superpass").get();
        assertThat(user.getUsername()).isEqualTo("superadmin");
        assertThat(userRoleStore.getUserJidsByRole(UserRole.SUPERADMIN)).containsExactly(user.getJid());

        superadminCreator.ensureSuperadminExists();

        // superadmin still exists, and holds SUPERADMIN once
        user = userStore.getUserByUsernameAndPassword("superadmin", "superpass").get();
        assertThat(user.getUsername()).isEqualTo("superadmin");
        assertThat(userRoleStore.getUserJidsByRole(UserRole.SUPERADMIN)).containsExactly(user.getJid());
    }
}
