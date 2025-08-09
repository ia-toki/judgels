package judgels.michael.actor;

import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.UserStore;
import judgels.jophiel.user.role.UserRoleStore;
import judgels.service.actor.PerRequestActorProvider;

public class ActorChecker {
    private final SessionStore sessionStore;
    private final UserStore userStore;
    private final UserRoleStore userRoleStore;

    @Inject
    public ActorChecker(SessionStore sessionStore, UserStore userStore, UserRoleStore userRoleStore) {
        this.sessionStore = sessionStore;
        this.userStore = userStore;
        this.userRoleStore = userRoleStore;
    }

    public Actor check(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JUDGELS_TOKEN")) {
                    Optional<Session> session = sessionStore.getSessionByToken(cookie.getValue());
                    if (session.isPresent()) {
                        String userJid = session.get().getUserJid();
                        Optional<User> user = userStore.getUserByJid(userJid);
                        if (user.isPresent()) {
                            PerRequestActorProvider.setJid(userJid);
                            UserRole role = userRoleStore.getRole(userJid);
                            return new Actor.Builder()
                                    .userJid(userJid)
                                    .username(user.get().getUsername())
                                    .role(role)
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
