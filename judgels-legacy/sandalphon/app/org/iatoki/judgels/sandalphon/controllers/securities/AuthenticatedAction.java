package org.iatoki.judgels.sandalphon.controllers.securities;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public final class AuthenticatedAction extends Action<Authenticated> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            for (Class<? extends Security.Authenticator> authenticatorClass : this.configuration.value()) {
                Security.Authenticator var2 = (Security.Authenticator) authenticatorClass.newInstance();
                String var3 = var2.getUsername(context);
                if (var3 == null) {
                    Result var12 = var2.onUnauthorized(context);
                    return F.Promise.pure(var12);
                } else {
                    try {
                        context.request().setUsername(var3);
                    } finally {
                        context.request().setUsername((String) null);
                    }
                }
            }
            return this.delegate.call(context);
        } catch (RuntimeException var10) {
            throw var10;
        } catch (Throwable var11) {
            throw var11;
        }
    }
}
