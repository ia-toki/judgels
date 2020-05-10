package org.iatoki.judgels.jophiel.controllers;

import org.iatoki.judgels.jophiel.JophielSessionUtils;
import org.iatoki.judgels.jophiel.routes;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
    @Override
    public String getUsername(Http.Context context) {
        if (!JophielSessionUtils.isSessionValid(context)) {
            context.session().remove("username");
            return null;
        }

        return context.session().get("username");
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return redirect(routes.JophielClientController.login());
    }
}
