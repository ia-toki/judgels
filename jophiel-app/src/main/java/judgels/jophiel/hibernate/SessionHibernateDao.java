package judgels.jophiel.hibernate;

import java.time.Clock;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.SessionModel_;
import judgels.persistence.ActorProvider;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import org.hibernate.SessionFactory;

@Singleton
public class SessionHibernateDao extends UnmodifiableHibernateDao<SessionModel> implements SessionDao {
    @Inject
    public SessionHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public Optional<SessionModel> selectByToken(String token) {
        return selectByUniqueColumn(SessionModel_.token, token);
    }

    @Override
    public List<SessionModel> selectAllByUserJid(String userJid) {
        return selectAll(new FilterOptions.Builder<SessionModel>()
                .putColumnsEq(SessionModel_.userJid, userJid)
                .build()).getData();
    }
}
