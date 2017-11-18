package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.session.SessionDao;
import judgels.jophiel.session.SessionModel;
import judgels.jophiel.session.SessionModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

public class SessionHibernateDao extends HibernateDao<SessionModel> implements SessionDao {
    @Inject
    public SessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<SessionModel> findByToken(String token) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<SessionModel> cq = cb.createQuery(getEntityClass());
        Root<SessionModel> root = cq.from(getEntityClass());
        cq.where(cb.equal(root.get(SessionModel_.token), token));
        return currentSession().createQuery(cq).uniqueResultOptional();
    }
}
