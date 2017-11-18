package judgels.jophiel.session;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.session.Session;

public class SessionStore {
    private final SessionDao sessionDao;

    @Inject
    public SessionStore(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public Optional<Session> findSessionByToken(String token) {
        return sessionDao.findByToken(token).map(SessionStore::fromModel);
    }

    public Session createSession(String token, String userJid) {
        SessionModel model = new SessionModel();
        toModel(token, userJid, model);
        return fromModel(sessionDao.insert(model));
    }

    private static Session fromModel(SessionModel model) {
        return Session.of(model.token, model.userJid);
    }

    private static void toModel(String token, String userJid, SessionModel model) {
        model.token = token;
        model.userJid = userJid;
    }
}
