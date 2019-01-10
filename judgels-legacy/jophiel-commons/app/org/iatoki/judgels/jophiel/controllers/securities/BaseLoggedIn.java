package org.iatoki.judgels.jophiel.controllers.securities;

import org.iatoki.judgels.jophiel.JophielSessionUtils;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public abstract class BaseLoggedIn extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context context) {
        if (!JophielSessionUtils.isSessionValid(context)) {
            removeAuthenticationSessions(context);
            return null;
        }

        return context.session().get("username");
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return redirect(getRedirectCall(context));
    }

    public abstract Call getRedirectCall(Http.Context context);

    public abstract void removeAuthenticationSessions(Http.Context context);
}
