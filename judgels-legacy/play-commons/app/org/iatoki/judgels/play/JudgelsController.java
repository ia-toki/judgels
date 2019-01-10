package org.iatoki.judgels.play;

import play.Play;
import play.mvc.Result;

import javax.inject.Singleton;
import java.io.File;

@Singleton
public final class JudgelsController extends AbstractJudgelsController {

    public Result favicon() {
        File faviconFile = new File(Play.application().getFile("external-assets"), "favicon.ico");

        if (faviconFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(faviconFile);
        } else {
            return notFound();
        }
    }

    public Result logo() {
        File logoFile = new File(Play.application().getFile("external-assets"), "logo.png");

        if (logoFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(logoFile);
        } else {
            return notFound();
        }
    }

    public Result coloredLogo() {
        File logoFile = new File(Play.application().getFile("external-assets"), "logo-colored.png");

        if (logoFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(logoFile);
        } else {
            return notFound();
        }
    }

    public Result changeLanguage(String newLang) {
        ctx().changeLang(newLang);
        return redirect(request().getHeader("Referer"));
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
