package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.dao.SessionDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.UnmodifiableHibernateDao;
import judgels.persistence.model.SessionModel;
import judgels.persistence.model.SessionModel_;

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
    public List<SessionModel> selectAllByUserJids(Collection<String> userJids) {
        return select().where(columnIn(SessionModel_.userJid, userJids)).all();
    }

    @Override
    public List<SessionModel> selectAllOlderThan(Instant time) {
        return select()
                .where((cb, cq, root) -> cb.lessThan(root.get(SessionModel_.createdAt), time))
                .all();
    }
}
