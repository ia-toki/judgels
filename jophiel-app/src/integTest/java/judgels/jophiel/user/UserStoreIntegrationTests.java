package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.InputStream;
import java.util.List;
import judgels.fs.FileSystem;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.hibernate.UserHibernateDao;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
class UserStoreIntegrationTests {
    private UserStore store;

    @BeforeEach void before(SessionFactory sessionFactory) {
        UserDao dao = new UserHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new UserStore(dao, new FakeFs());
    }

    @Test void can_do_basic_crud() {
        assertThat(store.findUserByUsername("username")).isEmpty();

        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        store.createUser(userData);

        User user = store.findUserByUsername("username").get();
        assertThat(user.getJid()).isNotEmpty();
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getEmail()).isEqualTo("email@domain.com");
        assertThat(user.getAvatarUrl()).isEmpty();

        assertThat(store.findUserByJid(user.getJid())).contains(user);

        userData = new UserData.Builder()
                .username("new.username")
                .password("new.password")
                .email("new.email@domain.com")
                .build();

        user = store.updateUser(user.getJid(), userData).get();
        assertThat(user.getUsername()).isEqualTo("new.username");
        assertThat(user.getEmail()).isEqualTo("new.email@domain.com");
    }

    @Test void can_update_avatar() {
        UserData userData = new UserData.Builder()
                .username("username")
                .password("password")
                .email("email@domain.com")
                .build();
        User user = store.createUser(userData);

        store.updateUserAvatar(user.getJid(), "avatar.jpg");

        user = store.findUserByJid(user.getJid()).get();
        assertThat(user.getAvatarUrl()).contains("/fake/avatar.jpg");
    }

    @Test void username_has_unique_constraint() {
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

    @Test void email_has_unique_constraint() {
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

    @Test void can_get_by_term() {
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
                .containsExactly("andi", "dimas");
    }

    static class FakeFs implements FileSystem {
        @Override
        public void uploadPublicFile(InputStream file, List<String> destDirPath, String destFilename) {}

        @Override
        public String getPublicFileUrl(List<String> filePath) {
            return "/fake/" + filePath.get(0);
        }
    }
}
