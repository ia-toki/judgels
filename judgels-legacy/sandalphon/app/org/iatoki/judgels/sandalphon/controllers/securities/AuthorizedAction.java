package org.iatoki.judgels.sandalphon.controllers.securities;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Arrays;
import java.util.List;

public final class AuthorizedAction extends Action<Authorized> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        if (AuthorizationType.ALLOW.equals(configuration.type())) {
            List<String> allowedRoles = Arrays.asList(configuration.value());
            List<String> currentRoles = Arrays.asList(context.session().get("role").split(","));

            boolean check = true;
            int i = 0;
            while ((check) && (i < currentRoles.size())) {
                if (allowedRoles.contains(currentRoles.get(i))) {
                    check = false;
                } else {
                    ++i;
                }
            }

            if (!check) {
                return delegate.call(context);
            } else {
                return F.Promise.pure(forbidden());
            }
        } else if (AuthorizationType.RESTRICT.equals(configuration.type())) {
            List<String> restrictedRoles = Arrays.asList(configuration.value());
            List<String> currentRoles = Arrays.asList(context.session().get("role").split(","));

            boolean check = true;
            int i = 0;
            while ((check) && (i < currentRoles.size())) {
                if (restrictedRoles.contains(currentRoles.get(i))) {
                    check = false;
                } else {
                    ++i;
                }
            }

            if (!check) {
                return F.Promise.pure(forbidden());
            } else {
                return delegate.call(context);
            }
        } else {
            return null;
        }
    }
}
