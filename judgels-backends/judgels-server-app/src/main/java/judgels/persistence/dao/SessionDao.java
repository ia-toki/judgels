package judgels.persistence.dao;

import jakarta.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import judgels.persistence.DaoData;
import judgels.persistence.UnmodifiableDao;
import judgels.persistence.model.SessionModel;
import judgels.persistence.model.SessionModel_;

public class SessionDao extends UnmodifiableDao<SessionModel> {
    @Inject
    public SessionDao(DaoData data) {
        super(data);
    }

    public Optional<SessionModel> selectByToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return select().where(columnEq(SessionModel_.token, token)).unique();
    }

    public List<SessionModel> selectAllByUserJid(String userJid) {
        return select().where(columnEq(SessionModel_.userJid, userJid)).all();
    }

    public List<SessionModel> selectAllByUserJids(Collection<String> userJids) {
        return select().where(columnIn(SessionModel_.userJid, userJids)).all();
    }

    public List<SessionModel> selectAllOlderThan(Instant time) {
        return select()
                .where((cb, cq, root) -> cb.lessThan(root.get(SessionModel_.createdAt), time))
                .all();
    }
}
