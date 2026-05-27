package judgels.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import judgels.persistence.SessionModel;
import judgels.persistence.TestClock;
import judgels.persistence.hibernate.WithHibernateSession;
import judgels.user.BaseUserIntegrationTests;
import judgels.user.UserIntegrationTestComponent;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {SessionModel.class})
public class SessionCleanerIntegrationTests extends BaseUserIntegrationTests {
    private TestClock clock;
    private SessionStore store;
    private SessionCleaner cleaner;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        clock = new TestClock();
        UserIntegrationTestComponent component = createComponent(sessionFactory, clock);
        store = component.sessionStore();
        cleaner = new SessionCleaner(clock, store);
    }

    @Test
    void run() {
        store.createSession("token123", "userJid1");
        clock.tick(Duration.ofDays(30));
        store.createSession("token456", "userJid1");
        clock.tick(Duration.ofDays(3 * 30));
        store.createSession("token789", "userJid1");
        store.createSession("token000", "userJid2");
        clock.tick(Duration.ofDays(4 * 30));

        cleaner.run();

        assertThat(store.getSessionByToken("token123")).isEmpty();
        assertThat(store.getSessionByToken("token456")).isEmpty();
        assertThat(store.getSessionByToken("token789")).isNotEmpty();
        assertThat(store.getSessionByToken("token000")).isNotEmpty();
    }
}
