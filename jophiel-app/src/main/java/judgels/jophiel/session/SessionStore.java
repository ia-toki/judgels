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

    public Session createSession(String token, String userJid) {
        SessionModel model = new SessionModel();
        toModel(token, userJid, model);
        return fromModel(sessionDao.insert(model));
    }

    public Optional<Session> findSessionByToken(String token) {
        return sessionDao.findByToken(token).map(SessionStore::fromModel);
    }

    public void deleteSessionByToken(String token) {
        sessionDao.findByToken(token).ifPresent(sessionDao::delete);
    }

    private static Session fromModel(SessionModel model) {
        return Session.of(model.token, model.userJid);
    }

    private static void toModel(String token, String userJid, SessionModel model) {
        model.token = token;
        model.userJid = userJid;
    }
}
