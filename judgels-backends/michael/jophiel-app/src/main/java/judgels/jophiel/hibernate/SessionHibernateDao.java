package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.SessionModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

@Singleton
public class SessionHibernateDao extends UnmodifiableHibernateDao<SessionModel> implements SessionDao {
    @Inject
    public SessionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public Optional<SessionModel> selectByToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return selectByUniqueColumn(SessionModel_.token, token);
    }

    @Override
    public List<SessionModel> selectAllByUserJid(String userJid) {
        return selectAll(new FilterOptions.Builder<SessionModel>()
                .putColumnsEq(SessionModel_.userJid, userJid)
                .build());
    }

    @Override
    public List<SessionModel> selectAllByUserJids(Set<String> userJids) {
        return selectAll(new FilterOptions.Builder<SessionModel>()
                .putColumnsIn(SessionModel_.userJid, userJids)
                .build());
    }

    @Override
    public List<SessionModel> selectAllOlderThan(Instant time) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<SessionModel> cq = cb.createQuery(SessionModel.class);
        Root<SessionModel> root = cq.from(getEntityClass());

        cq.where(cb.lessThan(root.get(SessionModel_.createdAt), time));
        return currentSession().createQuery(cq).getResultList();
    }
}
