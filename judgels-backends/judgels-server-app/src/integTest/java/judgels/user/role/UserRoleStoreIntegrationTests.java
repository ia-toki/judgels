package judgels.user.role;

import static judgels.api.user.role.UserRole.CONTEST_ADMIN;
import static judgels.api.user.role.UserRole.PROBLEM_ADMIN;
import static judgels.api.user.role.UserRole.SUPERADMIN;
import static judgels.api.user.role.UserRole.SYSTEM_ADMIN;
import static judgels.api.user.role.UserRole.TRAINING_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import judgels.api.user.role.UserRole;
import judgels.persistence.WithHibernateSession;
import judgels.persistence.model.UserRoleModel;
import judgels.user.BaseUserIntegrationTests;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserRoleModel.class})
class UserRoleStoreIntegrationTests extends BaseUserIntegrationTests {
    private UserRoleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        store = createComponent(sessionFactory).userRoleStore();
    }

    @Test
    void set_roles() {
        assertThat(store.getRoles("andi")).isEmpty();

        store.setRoles("andi", Set.of(PROBLEM_ADMIN, CONTEST_ADMIN));
        assertThat(store.getRoles("andi")).containsExactlyInAnyOrder(PROBLEM_ADMIN, CONTEST_ADMIN);

        store.setRoles("andi", Set.of(CONTEST_ADMIN, TRAINING_ADMIN));
        assertThat(store.getRoles("andi")).containsExactlyInAnyOrder(CONTEST_ADMIN, TRAINING_ADMIN);
        assertThat(store.getUserJidsByRole(PROBLEM_ADMIN)).isEmpty();

        store.setRoles("andi", Set.of());
        assertThat(store.getRoles("andi")).isEmpty();
    }

    @Test
    void add_roles() {
        store.addRoles("andi", Set.of(CONTEST_ADMIN));
        store.addRoles("andi", Set.of(CONTEST_ADMIN, TRAINING_ADMIN));
        store.addRoles("budi", Set.of(CONTEST_ADMIN));

        assertThat(store.getRoles("andi")).containsExactlyInAnyOrder(CONTEST_ADMIN, TRAINING_ADMIN);
        assertThat(store.getUserJidsByRole(CONTEST_ADMIN)).containsExactly("andi", "budi");
    }

    @Test
    void get_roles_expands_superadmin() {
        store.addRoles("superadmin", Set.of(SUPERADMIN));

        assertThat(store.getRoles("superadmin")).containsExactlyInAnyOrder(UserRole.values());
        assertThat(store.getUserJidsByRole(SYSTEM_ADMIN)).isEmpty();
    }
}
