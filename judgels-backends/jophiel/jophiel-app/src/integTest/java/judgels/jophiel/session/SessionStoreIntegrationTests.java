package judgels.jophiel.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import judgels.jophiel.AbstractIntegrationTests;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.persistence.SessionModel;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {SessionModel.class})
class SessionStoreIntegrationTests extends AbstractIntegrationTests {
    private SessionStore store;
    private org.hibernate.Session currentSession;

    @BeforeEach
    void setUpSession(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = createComponent(sessionFactory);
        store = component.sessionStore();
        currentSession = sessionFactory.getCurrentSession();
    }

    @Test
    void crud_flow() {
        assertThat(store.getSessionByToken("token123")).isEmpty();

        store.createSession("token123", "userJid");
        store.createSession("token223", "userJid");
        store.createSession("token323", "userJid2");

        Session session = store.getSessionByToken("token123").get();
        assertThat(session.getToken()).isEqualTo("token123");
        assertThat(session.getUserJid()).isEqualTo("userJid");

        store.deleteSessionsByUserJid("userJid");
        currentSession.flush();

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
}
