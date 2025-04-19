package judgels.service.actor;

import static judgels.service.actor.Actors.GUEST;

import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.Optional;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.service.api.actor.AuthHeader;
import org.eclipse.jetty.server.Response;

public class ActorChecker {
    private final SessionStore sessionStore;

    @Inject
    public ActorChecker(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public String check(Optional<AuthHeader> authHeader) {
        PerRequestActorProvider.clearJid();

        if (!authHeader.isPresent()) {
            return GUEST;
        }
        return check(authHeader.get());
    }

    public String check(@Nullable AuthHeader authHeader) {
        PerRequestActorProvider.clearJid();

        if (authHeader == null) {
            throw new NotAuthorizedException(Response.SC_UNAUTHORIZED);
        }

        Session session = sessionStore.getSessionByToken(authHeader.getBearerToken())
                .orElseThrow(() -> new NotAuthorizedException(Response.SC_UNAUTHORIZED));

        String actorJid = session.getUserJid();

        PerRequestActorProvider.setJid(actorJid);

        return actorJid;
    }

    public void clear() {
        PerRequestActorProvider.clearJid();
        PerRequestActorProvider.clearIpAddress();
    }
}
