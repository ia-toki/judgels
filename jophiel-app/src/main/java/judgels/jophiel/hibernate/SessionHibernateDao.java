package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.SessionModel_;
import judgels.jophiel.session.SessionDao;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class SessionHibernateDao extends UnmodifiableHibernateDao<SessionModel> implements SessionDao {
    @Inject
    public SessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<SessionModel> findByToken(String token) {
        return selectByUniqueColumn(SessionModel_.token, token);
    }
}
