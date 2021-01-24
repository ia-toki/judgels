package org.iatoki.judgels.play.actor;

import javax.inject.Inject;
import judgels.service.actor.PerRequestActorProvider;
import org.iatoki.judgels.jophiel.JophielSessionUtils;
import play.mvc.Http;

public class ActorChecker {
    @Inject
    public ActorChecker() {}

    public String check(Http.Request req) {
        PerRequestActorProvider.clearJid();

        if (!JophielSessionUtils.isSessionValid(req)) {
            return null;
        }

        String userJid = JophielSessionUtils.getUserJid(req);
        if (userJid != null) {
            PerRequestActorProvider.setJid(userJid);
        }
        return userJid;
    }

    public void clear() {
        PerRequestActorProvider.clearJid();
        PerRequestActorProvider.clearIpAddress();
    }
}
