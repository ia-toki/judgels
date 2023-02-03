package judgels.jophiel.user.superadmin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.user.User;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.role.SuperadminRoleStore;
import judgels.jophiel.user.UserStore;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
public class SuperadminCreatorIntegrationTests extends AbstractIntegrationTests {
    private static final SuperadminCreatorConfiguration CONFIG = new SuperadminCreatorConfiguration.Builder()
            .initialPassword("superpass")
            .build();

    private UserStore userStore;
    private SuperadminRoleStore superadminRoleStore;
    private SuperadminCreator superadminCreator;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = createComponent(sessionFactory);
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
