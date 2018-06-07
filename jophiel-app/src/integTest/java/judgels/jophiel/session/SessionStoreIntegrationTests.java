package judgels.jophiel.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import judgels.jophiel.DaggerJophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestComponent;
import judgels.jophiel.JophielIntegrationTestHibernateModule;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.persistence.SessionModel;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {SessionModel.class})
class SessionStoreIntegrationTests {
    private SessionStore store;
    private org.hibernate.Session currentSession;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        JophielIntegrationTestComponent component = DaggerJophielIntegrationTestComponent.builder()
                .jophielIntegrationTestHibernateModule(new JophielIntegrationTestHibernateModule(sessionFactory))
                .build();
        store = component.sessionStore();
        currentSession = sessionFactory.getCurrentSession();
    }

    @Test
    void can_do_basic_crud() {
        assertThat(store.findSessionByToken("token123")).isEmpty();

        store.createSession("token123", "userJid");
        store.createSession("token223", "userJid");
        store.createSession("token323", "userJid2");

        Session session = store.findSessionByToken("token123").get();
        assertThat(session.getToken()).isEqualTo("token123");
        assertThat(session.getUserJid()).isEqualTo("userJid");

        store.deleteSessionsByUserJid("userJid");
        currentSession.flush();

        assertThat(store.findSessionByToken("token123")).isEmpty();
        assertThat(store.findSessionByToken("token223")).isEmpty();
        assertThat(store.findSessionByToken("token323")).isNotEmpty();
    }

    @Test
    void token_has_unique_constraint() {
        store.createSession("token123", "userJid1");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createSession("token123", "userJid2"));
    }
}
