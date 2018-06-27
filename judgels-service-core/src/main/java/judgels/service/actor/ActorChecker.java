package judgels.service.actor;

import static judgels.service.actor.Actors.GUEST;

import java.util.Optional;
import javax.annotation.Nullable;
import javax.ws.rs.NotAuthorizedException;
import judgels.service.api.actor.ActorExtractor;
import judgels.service.api.actor.AuthHeader;
import org.eclipse.jetty.server.Response;

public class ActorChecker {
    private final ActorExtractor actorExtractor;

    public ActorChecker(ActorExtractor actorExtractor) {
        this.actorExtractor = actorExtractor;
    }

    public String check(Optional<AuthHeader> authHeader) {
        if (!authHeader.isPresent()) {
            return GUEST;
        }
        return check(authHeader.get());
    }

    public String check(@Nullable AuthHeader authHeader) {
        if (authHeader == null) {
            throw new NotAuthorizedException(Response.SC_UNAUTHORIZED);
        }

        Optional<String> actorJid = actorExtractor.extractJid(authHeader);

        if (actorJid.isPresent()) {
            PerRequestActorProvider.setJid(actorJid.get());
            return actorJid.get();
        } else {
            PerRequestActorProvider.clearJid();
            throw new NotAuthorizedException(Response.SC_UNAUTHORIZED);
        }
    }
}
