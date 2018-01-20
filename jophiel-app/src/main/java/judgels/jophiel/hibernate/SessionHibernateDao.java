package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import judgels.jophiel.session.SessionDao;
import judgels.jophiel.session.SessionModel;
import judgels.jophiel.session.SessionModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

public class SessionHibernateDao extends UnmodifiableHibernateDao<SessionModel> implements SessionDao {
    public SessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<SessionModel> findByToken(String token) {
        return selectByUniqueColumn(SessionModel_.token, token);
    }
}
