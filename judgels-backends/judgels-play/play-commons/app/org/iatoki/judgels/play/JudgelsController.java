package org.iatoki.judgels.play;

import controllers.routes;
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
        response().setCookie("sidebar", "true");
        return redirect(request().getHeader("Referer"));
    }

    public Result hideSidebar() {
        response().setCookie("sidebar", "false");
        return redirect(request().getHeader("Referer"));
    }

    public Result enterFullscreen() {
        response().setCookie("fullscreen", "true");
        return redirect(request().getHeader("Referer"));
    }

    public Result exitFullscreen() {
        response().setCookie("fullscreen", "false");
        return redirect(request().getHeader("Referer"));
    }

    public Result checkHealth() {
        return ok("");
    }
}
