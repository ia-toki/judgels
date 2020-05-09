package judgels.jophiel.session;

import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.persistence.SessionDao;
import judgels.jophiel.persistence.SessionModel;
import judgels.jophiel.play.PlaySessionDao;
import judgels.jophiel.play.PlaySessionModel;

public class SessionStore {
    private final SessionDao sessionDao;
    private final PlaySessionDao playSessionDao;

    @Inject
    public SessionStore(SessionDao sessionDao, PlaySessionDao playSessionDao) {
        this.sessionDao = sessionDao;
        this.playSessionDao = playSessionDao;
    }

    public Session createSession(String token, String userJid) {
        SessionModel model = new SessionModel();
        toModel(token, userJid, model);
        return fromModel(sessionDao.insert(model));
    }

    public void createAuthCode(String token, String authCode) {
        PlaySessionModel model = new PlaySessionModel();
        model.token = token;
        model.authCode = authCode;
        playSessionDao.insert(model);
    }

    public Optional<Session> getSessionByToken(String token) {
        return sessionDao.selectByToken(token).map(SessionStore::fromModel);
    }

    public Optional<Session> getSessionByAuthCode(String authCode) {
        return playSessionDao.getByAuthCode(authCode).flatMap(legacyModel ->
                sessionDao.selectByToken(legacyModel.token).map(SessionStore::fromModel));
    }

    public void deleteAuthCode(String authCode) {
        playSessionDao.getByAuthCode(authCode).ifPresent(playSessionDao::delete);
    }

    public void deleteSessionByToken(String token) {
        sessionDao.selectByToken(token).ifPresent(sessionDao::delete);
    }

    public void deleteSessionsByUserJid(String userJid) {
        sessionDao.selectAllByUserJid(userJid).forEach(sessionDao::delete);
    }

    private static Session fromModel(SessionModel model) {
        return Session.of(model.token, model.userJid);
    }

    private static void toModel(String token, String userJid, SessionModel model) {
        model.token = token;
        model.userJid = userJid;
    }
}
