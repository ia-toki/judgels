package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import judgels.jophiel.DaggerJophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestHibernateModule;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.persistence.UserModel;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
class UserStoreIntegrationTests {
    private UserStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = DaggerJophielIntegrationTestComponent.builder()
                .jophielIntegrationTestHibernateModule(new JophielIntegrationTestHibernateModule(sessionFactory))
                .build();
        store = component.userStore();
    }

    @Test
    void can_do_basic_crud() {
        assertThat(store.getUserByUsername("username")).isEmpty();

        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        store.createUser(userData);

        User user = store.getUserByUsername("username").get();
        assertThat(user.getJid()).isNotEmpty();
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(store.getUserEmailByJid(user.getJid())).contains("email@domain.com");

        assertThat(store.getUserAvatarUrl(user.getJid())).isEmpty();

        assertThat(store.getUserByJid(user.getJid())).contains(user);

        userData = new UserData.Builder()
                .username("new.username")
                .password("new.password")
                .email("new.email@domain.com")
                .build();

        user = store.updateUser(user.getJid(), userData).get();
        assertThat(user.getUsername()).isEqualTo("new.username");
        assertThat(store.getUserEmailByJid(user.getJid())).contains("new.email@domain.com");

        UserData nanoData = new UserData.Builder()
                .username("nano")
                .password("pass")
                .email("nano@domain.com")
                .build();
        store.createUser(nanoData);

        User nano = store.getUserByUsername("nano").get();

        UserData budiData = new UserData.Builder()
                .username("budi")
                .password("pass")
                .email("budi@domain.com")
                .build();
        store.createUser(budiData);

        User budi = store.getUserByUsername("budi").get();

        Page<User> users = store.getUsers(SelectionOptions.DEFAULT_PAGED);
        assertThat(users.getData()).containsExactly(budi, nano, user);
    }

    @Test
    void can_update_avatar() {
        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        User user = store.createUser(userData);

        store.updateUserAvatar(user.getJid(), "avatar.jpg");

        user = store.getUserByJid(user.getJid()).get();
        assertThat(store.getUserAvatarUrl(user.getJid())).contains("/fake/avatar.jpg");
    }

    @Test
    void username_has_unique_constraint() {
        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        store.createUser(userData);

        UserData newUserData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("new.email@domain.com")
                .build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createUser(newUserData));
    }

    @Test
    void email_has_unique_constraint() {
        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        store.createUser(userData);

        UserData newUserData = new UserData.Builder()
                .username("new.username")
                .password("new.password")
                .email("email@domain.com")
                .build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createUser(newUserData));
    }

    @Test
    void can_get_by_term() {
        UserData userData = new UserData.Builder()
                .username("andi")
                .password("password")
                .email("andi@domain.com")
                .build();
        store.createUser(userData);

        userData = new UserData.Builder()
                .username("dimas")
                .password("password")
                .email("dimas@domain.com")
                .build();
        store.createUser(userData);

        userData = new UserData.Builder()
                .username("ani")
                .password("password")
                .email("ani@domain.com")
                .build();
        store.createUser(userData);

        assertThat(store.getUsersByTerm("di"))
                .extracting("username")
                .containsExactly("dimas", "andi");
    }
}
