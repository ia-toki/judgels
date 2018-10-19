package judgels.jophiel.user.password;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.jophiel.DaggerJophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestHibernateModule;
import judgels.jophiel.JophielIntegrationTestPersistenceModule;
import judgels.jophiel.persistence.UserResetPasswordModel;
import judgels.persistence.TestClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserResetPasswordModel.class})
class UserResetPasswordStoreIntegrationTests {
    private static final String USER_JID = "userJid";

    private Session currentSession;
    private TestClock clock;
    private UserResetPasswordStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        currentSession = sessionFactory.getCurrentSession();
        clock = new TestClock();

        JophielIntegrationTestComponent component = DaggerJophielIntegrationTestComponent.builder()
                .jophielIntegrationTestHibernateModule(new JophielIntegrationTestHibernateModule(sessionFactory))
                .jophielIntegrationTestPersistenceModule(new JophielIntegrationTestPersistenceModule(clock))
                .build();

        store = component.userResetPasswordStore();
    }

    @Test
    void can_generate_find_consume_code() {
        assertThat(store.consumeEmailCode("code", Duration.ofHours(1))).isEmpty();

        store.generateEmailCode("userJid2", Duration.ofHours(1));

        String code = store.generateEmailCode(USER_JID, Duration.ofHours(1));
        assertThat(store.consumeEmailCode(code, Duration.ofHours(1))).contains(USER_JID);

        currentSession.flush();
        clock.tick(Duration.ofSeconds(1));

        assertThat(store.consumeEmailCode(code, Duration.ofHours(1))).isEmpty();
        String newCode = store.generateEmailCode(USER_JID, Duration.ofHours(1));
        assertThat(newCode).isNotEqualTo(code);
    }

    @Test
    void same_code_is_returned_if_not_expired() {
        String code1 = store.generateEmailCode(USER_JID, Duration.ofHours(1));
        clock.tick(Duration.ofSeconds(1));
        String code2 = store.generateEmailCode(USER_JID, Duration.ofHours(1));
        assertThat(code1).isEqualTo(code2);
    }

    @Test
    void different_code_is_returned_if_expired() {
        String code1 = store.generateEmailCode(USER_JID, Duration.ofHours(1));
        clock.tick(Duration.ofSeconds(1));
        String code2 = store.generateEmailCode(USER_JID, Duration.ofMillis(700));
        assertThat(code1).isNotEqualTo(code2);
    }
}
