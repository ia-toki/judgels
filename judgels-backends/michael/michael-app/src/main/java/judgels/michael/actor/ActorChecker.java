package judgels.michael.actor;

import java.net.URI;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;

public class ActorChecker {
    private final SessionStore sessionStore;

    @Inject
    public ActorChecker(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public String check(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JOPHIEL_TOKEN")) {
                    Optional<Session> session = sessionStore.getSessionByToken(cookie.getValue());
                    if (session.isPresent()) {
                        return session.get().getUserJid();
                    }
                }
            }
        }

        throw new WebApplicationException(Response.seeOther(URI.create("/login")).build());
    }
}
