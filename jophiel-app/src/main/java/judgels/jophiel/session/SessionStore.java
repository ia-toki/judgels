package judgels.jophiel.session;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.legacy.session.LegacySessionDao;
import judgels.jophiel.legacy.session.LegacySessionModel;

public class SessionStore {
    private final SessionDao sessionDao;
    private final LegacySessionDao legacySessionDao;

    @Inject
    public SessionStore(SessionDao sessionDao, LegacySessionDao legacySessionDao) {
        this.sessionDao = sessionDao;
        this.legacySessionDao = legacySessionDao;
    }

    public Session createSession(String token, String userJid) {
        SessionModel model = new SessionModel();
        toModel(token, userJid, model);
        return fromModel(sessionDao.insert(model));
    }

    public void createAuthCode(String token, String authCode) {
        LegacySessionModel model = new LegacySessionModel();
        model.token = token;
        model.authCode = authCode;
        legacySessionDao.insert(model);
    }

    public Optional<Session> findSessionByToken(String token) {
        return sessionDao.findByToken(token).map(SessionStore::fromModel);
    }

    public Optional<Session> findSessionByAuthCode(String authCode) {
        return legacySessionDao.findByAuthCode(authCode).flatMap(legacyModel ->
            sessionDao.findByToken(legacyModel.token).map(SessionStore::fromModel));
    }

    public void deleteAuthCode(String authCode) {
        legacySessionDao.findByAuthCode(authCode).ifPresent(legacySessionDao::delete);
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
