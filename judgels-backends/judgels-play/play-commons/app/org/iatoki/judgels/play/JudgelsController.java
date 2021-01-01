package org.iatoki.judgels.play;

import controllers.routes;
import play.mvc.Http.Cookie;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public final class JudgelsController extends AbstractJudgelsController {

    public Result favicon() {
        return redirect(routes.Assets.at("lib/playcommons/images/favicon.ico"));
    }

    public Result logo() {
        return redirect(routes.Assets.at("lib/playcommons/images/logo.png"));
    }

    public Result coloredLogo() {
        return redirect(routes.Assets.at("lib/playcommons/images/logo-colored.png"));
    }

    public Result showSidebar() {
        response().setCookie(Cookie.builder("sidebar", "true").build());
        return redirect(request().getHeader("Referer"));
    }

    public Result hideSidebar() {
        response().setCookie(Cookie.builder("sidebar", "false").build());
        return redirect(request().getHeader("Referer"));
    }

    public Result enterFullscreen() {
        response().setCookie(Cookie.builder("fullscreen", "true").build());
        return redirect(request().getHeader("Referer"));
    }

    public Result exitFullscreen() {
        response().setCookie(Cookie.builder("fullscreen", "false").build());
        return redirect(request().getHeader("Referer"));
    }

    public Result checkHealth() {
        return ok("");
    }
}
