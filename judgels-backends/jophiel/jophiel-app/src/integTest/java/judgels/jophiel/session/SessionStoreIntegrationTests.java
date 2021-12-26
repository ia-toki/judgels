package judgels.jophiel.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.persistence.SessionModel;
import judgels.persistence.TestClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {SessionModel.class})
class SessionStoreIntegrationTests extends AbstractIntegrationTests {
    private TestClock clock;
    private SessionStore store;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        clock = new TestClock();
        JophielIntegrationTestComponent component = createComponent(sessionFactory, clock);
        store = component.sessionStore();
    }

    @Test
    void crud_flow() {
        assertThat(store.getSessionByToken("token123")).isEmpty();

        store.createSession("token123", "userJid");
        store.createSession("token223", "userJid");
        store.createSession("token323", "userJid2");

        assertThat(store.getLatestSessionTimeByUserJids(ImmutableSet.of("userJid", "userJid2")))
                .containsOnlyKeys("userJid", "userJid2");

        Session session = store.getSessionByToken("token123").get();
        assertThat(session.getToken()).isEqualTo("token123");
        assertThat(session.getUserJid()).isEqualTo("userJid");

        store.deleteSessionsByUserJid("userJid");

        assertThat(store.getSessionByToken("token123")).isEmpty();
        assertThat(store.getSessionByToken("token223")).isEmpty();
        assertThat(store.getSessionByToken("token323")).isNotEmpty();
    }

    @Test
    void token_has_unique_constraint() {
        store.createSession("token123", "userJid1");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createSession("token123", "userJid2"));
    }

    @Test
    void delete_sessions_older_than() {
        store.createSession("token123", "userJid1");
        clock.tick(Duration.ofHours(1));
        store.createSession("token456", "userJid1");
        clock.tick(Duration.ofHours(2));
        store.createSession("token789", "userJid1");
        store.createSession("token000", "userJid2");
        clock.tick(Duration.ofHours(2));

        store.deleteSessionsOlderThan(clock.instant().minus(Duration.ofHours(3)));

        assertThat(store.getSessionByToken("token123")).isEmpty();
        assertThat(store.getSessionByToken("token456")).isEmpty();
        assertThat(store.getSessionByToken("token789")).isNotEmpty();
        assertThat(store.getSessionByToken("token000")).isNotEmpty();
    }
}
