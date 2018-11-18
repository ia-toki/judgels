package judgels.uriel.role;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.uriel.AbstractIntegrationTests;
import judgels.uriel.UrielIntegrationTestComponent;
import judgels.uriel.api.admin.Admin;
import judgels.uriel.persistence.AdminRoleModel;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AdminRoleModel.class})
class AdminRoleStoreIntegrationTests extends AbstractIntegrationTests {
    private AdminRoleStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        UrielIntegrationTestComponent component = createComponent(sessionFactory);

        store = component.adminRoleStore();
    }

    @Test
    void crud_flow() {
        assertThat(store.upsertAdmin("userJidA")).isTrue();
        assertThat(store.upsertAdmin("userJidB")).isTrue();
        assertThat(store.upsertAdmin("userJidA")).isFalse();

        Page<Admin> admins = store.getAdmins(Optional.empty());
        assertThat(admins.getPage()).containsOnly(
                new Admin.Builder().userJid("userJidA").build(),
                new Admin.Builder().userJid("userJidB").build());


        assertThat(store.deleteAdmin("userJidA")).isTrue();
        assertThat(store.deleteAdmin("userJidC")).isFalse();

        admins = store.getAdmins(Optional.empty());
        assertThat(admins.getPage()).containsOnly(
                new Admin.Builder().userJid("userJidB").build());
    }
}
