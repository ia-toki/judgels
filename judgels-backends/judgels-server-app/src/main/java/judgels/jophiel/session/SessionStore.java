package judgels.jophiel.session;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.SessionModel;

public class SessionStore {
    private final SessionDao sessionDao;

    @Inject
    public SessionStore(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public Session createSession(String token, String userJid) {
        SessionModel model = new SessionModel();
        toModel(token, userJid, model);
        return fromModel(sessionDao.insert(model));
    }

    public Optional<Session> getSessionByToken(String token) {
        return sessionDao.selectByToken(token).map(SessionStore::fromModel);
    }

    public List<Session> getSessionsByUserJid(String userJid) {
        return sessionDao.selectAllByUserJid(userJid).stream()
                .map(SessionStore::fromModel)
                .collect(Collectors.toList());
    }

    public Map<String, Instant> getLatestSessionTimeByUserJids(Collection<String> userJids) {
        Map<String, Instant> timesMap = new HashMap<>();
        for (SessionModel m : sessionDao.selectAllByUserJids(userJids)) {
            timesMap.put(m.userJid, m.createdAt);
        }
        return ImmutableMap.copyOf(timesMap);
    }

    public void deleteSessionByToken(String token) {
        sessionDao.selectByToken(token).ifPresent(sessionDao::delete);
    }

    public void deleteSessionsByUserJid(String userJid) {
        sessionDao.selectAllByUserJid(userJid).forEach(sessionDao::delete);
    }

    public void deleteSessionsOlderThan(Instant time) {
        sessionDao.selectAllOlderThan(time).forEach(sessionDao::delete);
    }

    private static Session fromModel(SessionModel model) {
        return Session.of(model.token, model.userJid);
    }

    private static void toModel(String token, String userJid, SessionModel model) {
        model.token = token;
        model.userJid = userJid;
    }
}
