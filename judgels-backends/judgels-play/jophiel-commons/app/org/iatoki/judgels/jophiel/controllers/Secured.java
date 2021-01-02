package org.iatoki.judgels.jophiel.controllers;

import java.util.Optional;
import org.iatoki.judgels.jophiel.JophielSessionUtils;
import org.iatoki.judgels.jophiel.routes;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
    @Override
    public Optional<String> getUsername(Http.Request req) {
        if (!JophielSessionUtils.isSessionValid(req)) {
            return Optional.empty();
        }

        return req.session().getOptional("username");
    }

    @Override
    public Result onUnauthorized(Http.Request req) {
        return redirect(routes.JophielClientController.login());
    }
}
