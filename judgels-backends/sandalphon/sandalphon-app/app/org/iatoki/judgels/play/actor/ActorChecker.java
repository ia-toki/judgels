package org.iatoki.judgels.play.actor;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import judgels.service.actor.PerRequestActorProvider;
import play.mvc.Http;

public class ActorChecker {
    @Inject
    public ActorChecker() {}

    public String check(Http.Request req) {
        PerRequestActorProvider.clearJid();

        String userJid = req.session().getOptional("userJid")
                .orElseThrow(() -> new NotAuthorizedException(401));

        PerRequestActorProvider.setJid(userJid);

        return userJid;
    }

    public void clear() {
        PerRequestActorProvider.clearJid();
        PerRequestActorProvider.clearIpAddress();
    }
}
