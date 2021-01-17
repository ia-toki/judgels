package org.iatoki.judgels.play.controllers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.iatoki.judgels.play.EntityNotFoundException;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

public final class EntityNotFoundGuardAction extends Action<EntityNotFoundGuard> {

    @Override
    public CompletionStage<Result> call(Http.Context ctx) {
        try {
            return this.delegate.call(ctx);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof EntityNotFoundException) {
                return showEntityNotFound(e.getCause());
            } else {
                throw e;
            }
        }
    }

    private CompletionStage<Result> showEntityNotFound(Throwable e) {
        return CompletableFuture.supplyAsync(() -> Results.notFound());
    }
}
