package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.persistence.UserModel;
import judgels.persistence.api.Page;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
class UserStoreIntegrationTests extends AbstractIntegrationTests {
    private UserStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = createComponent(sessionFactory);
        store = component.userStore();
    }

    @Test
    void crud_flow() {
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
        assertThat(user.getEmail()).isEqualTo("email@domain.com");
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

        Page<User> users = store.getUsers(Optional.empty(), Optional.empty(), Optional.empty());
        assertThat(users.getPage()).containsExactly(budi, nano, user);
    }

    @Test
    void batch_create() {
        assertThat(store.getUserByUsername("andi")).isEmpty();
        assertThat(store.getUserByUsername("budi")).isEmpty();
        List<UserData> data = ImmutableList.of(
                new UserData.Builder()
                        .username("andi")
                        .password("pass")
                        .email("andi@domain.com")
                        .build(),
                new UserData.Builder()
                        .username("budi")
                        .password("pass")
                        .email("budi@domain.com")
                        .build());
        List<User> users = store.createUsers(data);

        assertThat(users.size()).isEqualTo(2);

        // System.out.println(users.get(0).getUsername());
        // System.out.println(users.get(1).getUsername());

        assertThat(store.getUserByUsername("andi")).isPresent();
        assertThat(store.getUserByUsername("budi")).isPresent();
    }

    @Test
    void update_avatar() {
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
    void get_by_term() {
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
