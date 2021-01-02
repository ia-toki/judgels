package org.iatoki.judgels.jophiel;

import play.mvc.Http;

public final class JophielSessionUtils {
    private JophielSessionUtils() {}

    public static String getSessionVersion() {
        return "4";
    }

    public static boolean isSessionValid(Http.Request req) {
        return getSessionVersion().equals(req.session().getOptional("version").orElse(""));
    }
}
