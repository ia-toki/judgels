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

        String actorJid = actorExtractor.extractJid(authHeader)
                .orElseThrow(() -> new NotAuthorizedException(Response.SC_UNAUTHORIZED));

        PerRequestActorProvider.setJid(actorJid);

        return actorJid;
    }

    public void clear() {
        PerRequestActorProvider.clearJid();
        PerRequestActorProvider.clearIpAddress();
    }
}
