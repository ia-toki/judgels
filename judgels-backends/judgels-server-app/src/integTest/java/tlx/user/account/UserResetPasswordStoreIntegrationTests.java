package tlx.user.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.persistence.TestClock;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.persistence.model.UserResetPasswordModel;
import judgels.user.BaseUserIntegrationTests;
import judgels.user.UserIntegrationTestComponent;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {UserResetPasswordModel.class})
public class UserResetPasswordStoreIntegrationTests extends BaseUserIntegrationTests {
    private static final String USER_JID = "userJid";

    private TestClock clock;
    private UserResetPasswordStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        clock = new TestClock();

        UserIntegrationTestComponent component = createComponent(sessionFactory, clock);

        store = component.userResetPasswordStore();
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
