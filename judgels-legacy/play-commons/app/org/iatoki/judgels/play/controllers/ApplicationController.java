package org.iatoki.judgels.play.controllers;

import play.Play;
import play.mvc.Result;

import java.io.File;

/**
 * @deprecated Has been renamed to JudgelsController
 */
@Deprecated
public final class ApplicationController extends AbstractJudgelsController {

    public static Result favicon() {
        File faviconFile = new File(Play.application().getFile("external-assets"), "favicon.ico");

        if (faviconFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(faviconFile);
        } else {
            return notFound();
        }
    }

    public static Result logo() {
        File logoFile = new File(Play.application().getFile("external-assets"), "logo.png");

        if (logoFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(logoFile);
        } else {
            return notFound();
        }
    }

    public static Result coloredLogo() {
        File logoFile = new File(Play.application().getFile("external-assets"), "logo-colored.png");

        if (logoFile.exists()) {
            response().setHeader(CACHE_CONTROL, "max-age=3600");
            return ok(logoFile);
        } else {
            return notFound();
        }
    }

    public static Result changeLanguage(String newLang) {
        ctx().changeLang(newLang);
        return redirect(request().getHeader("Referer"));
    }

    public static Result showSidebar() {
        response().setCookie("sidebar", "true");
        return redirect(request().getHeader("Referer"));
    }

    public static Result hideSidebar() {
        response().setCookie("sidebar", "false");
        return redirect(request().getHeader("Referer"));
    }

    public static Result enterFullscreen() {
        response().setCookie("fullscreen", "true");
        return redirect(request().getHeader("Referer"));
    }

    public static Result exitFullscreen() {
        response().setCookie("fullscreen", "false");
        return redirect(request().getHeader("Referer"));
    }

    public static Result checkHealth() {
        return ok("");
    }
}
