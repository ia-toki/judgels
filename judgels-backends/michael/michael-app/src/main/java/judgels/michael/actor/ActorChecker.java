package judgels.michael.actor;

import java.net.URI;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserStore;

public class ActorChecker {
    private final SessionStore sessionStore;
    private final UserStore userStore;

    @Inject
    public ActorChecker(SessionStore sessionStore, UserStore userStore) {
        this.sessionStore = sessionStore;
        this.userStore = userStore;
    }

    public Actor check(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JOPHIEL_TOKEN")) {
                    Optional<Session> session = sessionStore.getSessionByToken(cookie.getValue());
                    if (session.isPresent()) {
                        String userJid = session.get().getUserJid();
                        Optional<User> user = userStore.getUserByJid(userJid);
                        if (user.isPresent()) {
                            return new Actor.Builder()
                                    .userJid(userJid)
                                    .username(user.get().getUsername())
                                    .avatarUrl("/api/v2/users/" + userJid + "/avatar")
                                    .build();
                        }
                    }
                }
            }
        }

        throw new WebApplicationException(Response.seeOther(URI.create("/login")).build());
    }
}
