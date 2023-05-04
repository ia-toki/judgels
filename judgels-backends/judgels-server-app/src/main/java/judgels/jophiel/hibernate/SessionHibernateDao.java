package judgels.jophiel.hibernate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.persistence.SessionModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;

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
        return select().where(columnEq(SessionModel_.token, token)).unique();
    }

    @Override
    public List<SessionModel> selectAllByUserJid(String userJid) {
        return select().where(columnEq(SessionModel_.userJid, userJid)).all();
    }

    @Override
    public List<SessionModel> selectAllByUserJids(Set<String> userJids) {
        return select().where(columnIn(SessionModel_.userJid, userJids)).all();
    }

    @Override
    public List<SessionModel> selectAllOlderThan(Instant time) {
        return select()
                .where((cb, cq, root) -> cb.lessThan(root.get(SessionModel_.createdAt), time))
                .all();
    }
}
