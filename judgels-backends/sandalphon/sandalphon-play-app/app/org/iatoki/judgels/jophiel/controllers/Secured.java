package org.iatoki.judgels.jophiel.controllers;

import java.util.Optional;
import javax.inject.Inject;
import org.iatoki.judgels.jophiel.routes;
import org.iatoki.judgels.play.actor.ActorChecker;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
    private final ActorChecker actorChecker;

    @Inject
    public Secured(ActorChecker actorChecker) {
        this.actorChecker = actorChecker;
    }

    @Override
    public Optional<String> getUsername(Http.Request req) {
        return Optional.ofNullable(actorChecker.check(req));
    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return redirect(routes.JophielClientController.login());
    }
}
