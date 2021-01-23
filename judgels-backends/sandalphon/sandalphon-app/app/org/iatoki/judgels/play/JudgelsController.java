package org.iatoki.judgels.play;

import controllers.routes;
import javax.inject.Singleton;
import play.mvc.Result;

@Singleton
public final class JudgelsController extends AbstractJudgelsController {

    public Result favicon() {
        return redirect(routes.Assets.at("images/favicon.ico"));
    }

    public Result logo() {
        return redirect(routes.Assets.at("images/logo.png"));
    }

    public Result coloredLogo() {
        return redirect(routes.Assets.at("images/logo-colored.png"));
    }

    public Result checkHealth() {
        return ok("");
    }
}
