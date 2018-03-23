package judgels.jophiel.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import judgels.jophiel.api.session.Session;
import judgels.jophiel.hibernate.HibernateDaos.SessionHibernateDao;
import judgels.jophiel.legacy.session.LegacySessionDao;
import judgels.jophiel.legacy.session.LegacySessionHibernateDao;
import judgels.jophiel.persistence.Daos.SessionDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.persistence.FixedActorProvider;
import judgels.persistence.FixedClock;
import judgels.persistence.hibernate.WithHibernateSession;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WithHibernateSession(models = {SessionModel.class})
class SessionStoreIntegrationTests {
    private SessionStore store;

    @BeforeEach
    void before(SessionFactory sessionFactory) {
        SessionDao sessionDao = new SessionHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        LegacySessionDao legacySssionDao =
                new LegacySessionHibernateDao(sessionFactory, new FixedClock(), new FixedActorProvider());
        store = new SessionStore(sessionDao, legacySssionDao);
    }

    @Test
    void can_do_basic_crud() {
        assertThat(store.findSessionByToken("token123")).isEmpty();

        store.createSession("token123", "userJid");

        Session session = store.findSessionByToken("token123").get();
        assertThat(session.getToken()).isEqualTo("token123");
        assertThat(session.getUserJid()).isEqualTo("userJid");
    }

    @Test
    void token_has_unique_constraint() {
        store.createSession("token123", "userJid1");

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> store.createSession("token123", "userJid2"));
    }
}
