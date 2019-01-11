package org.iatoki.judgels.jerahmeel.controllers.securities;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class HasRole extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context context) {
        return JerahmeelUtils.getRolesFromSession();
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return redirect(org.iatoki.judgels.jerahmeel.routes.ApplicationController.authRole(context.request().uri()));
    }
}
