package judgels.training.account.password;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.persistence.TestClock;
import judgels.persistence.WithHibernateSession;
import judgels.persistence.model.AccountPasswordResetModel;
import judgels.training.BaseTrainingIntegrationTests;
import judgels.training.TrainingIntegrationTestComponent;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {AccountPasswordResetModel.class})
public class AccountPasswordResetStoreIntegrationTests extends BaseTrainingIntegrationTests {
    private static final String USER_JID = "userJid";

    private TestClock clock;
    private AccountPasswordResetStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        clock = new TestClock();

        TrainingIntegrationTestComponent component = createComponent(sessionFactory, clock);

        store = component.accountPasswordResetStore();
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
