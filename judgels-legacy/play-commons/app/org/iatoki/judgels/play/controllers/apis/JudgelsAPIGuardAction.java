package org.iatoki.judgels.play.controllers.apis;

import com.google.gson.JsonObject;
import org.iatoki.judgels.play.api.JudgelsAPIBadRequestException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.api.JudgelsAPIServerException;
import org.iatoki.judgels.play.api.JudgelsAPIForbiddenException;
import org.iatoki.judgels.play.api.JudgelsAPIUnauthorizedException;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import static play.mvc.Http.HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN;

public final class JudgelsAPIGuardAction extends Action<JudgelsAPIGuard> {

    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {
        try {
            context.response().setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");

            return this.delegate.call(context);

        } catch (JudgelsAPIServerException e) {
            JsonObject body = new JsonObject();
            body.addProperty("message", e.getMessage());

            if (e instanceof JudgelsAPIUnauthorizedException) {
                return F.Promise.promise(() -> Results.unauthorized(body.toString()));
            } else if (e instanceof JudgelsAPIBadRequestException) {
                return F.Promise.promise(() -> Results.badRequest(body.toString()));
            } else if (e instanceof JudgelsAPIForbiddenException) {
                return F.Promise.promise(() -> Results.forbidden(body.toString()));
            } else if (e instanceof JudgelsAPINotFoundException) {
                return F.Promise.promise(() -> Results.notFound(body.toString()));
            } else {
                return F.Promise.promise(() -> Results.internalServerError(body.toString()));
            }
        }
    }
}
