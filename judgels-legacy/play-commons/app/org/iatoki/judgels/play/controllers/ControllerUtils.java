package org.iatoki.judgels.play.controllers;

import play.mvc.Http;

/**
 * @deprecated Unused anymore.
 */
@Deprecated
public final class ControllerUtils {

    private ControllerUtils() {
        // prevent instantiation
    }

    public static String getCurrentUrl(Http.Request request) {
        return "http" + (request.secure() ? "s" : "") + "://" + request.host() + request.uri();
    }
}
