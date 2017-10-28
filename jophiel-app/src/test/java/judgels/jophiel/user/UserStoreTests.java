package judgels.jophiel.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import judgels.jophiel.api.user.User;
import judgels.jophiel.hibernate.UserHibernateDao;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserModel.class})
class UserStoreTests {
    private UserStore store;

    @BeforeEach void before(SessionFactory sessionFactory) {
        UserDao dao = new UserHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new UserStore(dao);
    }

    @Test void can_create_find_update() {
        User.Data userData = new User.Data.Builder()
                .username("username")
                .email("email@domain.com")
                .name("First Last")
                .build();
        store.createUser(userData);

        User user = store.findUserById(1).get();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getJid()).isNotEmpty();
        assertThat(user.getUsername()).isEqualTo("username");
        assertThat(user.getEmail()).isEqualTo("email@domain.com");
        assertThat(user.getName()).isEqualTo("First Last");

        assertThat(store.findUserByJid(user.getJid())).contains(user);

        userData = new User.Data.Builder()
                .username("new.username")
                .email("new.email@domain.com")
                .name("First Middle Last")
                .build();

        user = store.updateUser(user.getJid(), userData).get();
        assertThat(user.getUsername()).isEqualTo("new.username");
        assertThat(user.getEmail()).isEqualTo("new.email@domain.com");
        assertThat(user.getName()).isEqualTo("First Middle Last");
    }

    @Test void username_has_unique_constraint() {
        User.Data userData = new User.Data.Builder()
                .username("username")
                .email("email@domain.com")
                .name("First Last")
                .build();
        store.createUser(userData);

        User.Data newUserData = new User.Data.Builder()
                .username("username")
                .email("new.email@domain.com")
                .name("First Middle Last")
                .build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createUser(newUserData));
    }

    @Test void email_has_unique_constraint() {
        User.Data userData = new User.Data.Builder()
                .username("username")
                .email("email@domain.com")
                .name("First Last")
                .build();
        store.createUser(userData);

        User.Data newUserData = new User.Data.Builder()
                .username("new.username")
                .email("email@domain.com")
                .name("First Middle Last")
                .build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createUser(newUserData));
    }
}
