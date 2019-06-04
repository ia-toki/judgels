package org.iatoki.judgels.play.controllers;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public final class UnsupportedOperationGuardAction extends Action<UnsupportedOperationGuard> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            return this.delegate.call(context);
        } catch (UnsupportedOperationException e) {
            return showUnsupportedOperation(e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof UnsupportedOperationException) {
                return showUnsupportedOperation(e.getCause());
            } else {
                throw e;
            }
        }
    }

    private F.Promise<Result> showUnsupportedOperation(Throwable e) {
        return F.Promise.promise(() -> {
                return Results.badRequest();
            }
        );
    }
}
