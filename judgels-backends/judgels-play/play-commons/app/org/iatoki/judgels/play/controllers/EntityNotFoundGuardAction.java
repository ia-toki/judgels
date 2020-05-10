package org.iatoki.judgels.play.controllers;

import org.iatoki.judgels.play.EntityNotFoundException;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public final class EntityNotFoundGuardAction extends Action<EntityNotFoundGuard> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            return this.delegate.call(context);
        } catch (EntityNotFoundException e) {
            return showEntityNotFound(e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityNotFoundException) {
                return showEntityNotFound(e.getCause());
            } else {
                throw e;
            }
        }
    }

    private F.Promise<Result> showEntityNotFound(Throwable e) {
        return F.Promise.promise(() -> {
                return Results.notFound();
            }
        );
    }
}
