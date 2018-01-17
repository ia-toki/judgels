package judgels.service.actor;

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

    public String check(@Nullable AuthHeader authHeader) {
        if (authHeader == null) {
            throw new NotAuthorizedException(Response.SC_UNAUTHORIZED);
        }

        String actorJid = actorExtractor.extractJid(authHeader)
                .orElseThrow(() -> new NotAuthorizedException(Response.SC_UNAUTHORIZED));

        PerRequestActorProvider.setJid(actorJid);

        return actorJid;
    }
}
