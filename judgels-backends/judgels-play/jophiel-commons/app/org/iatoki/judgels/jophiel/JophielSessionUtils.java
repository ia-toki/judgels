package org.iatoki.judgels.jophiel;

import play.mvc.Http;

public final class JophielSessionUtils {
    private JophielSessionUtils() {}

    public static String getSessionVersion() {
        return "3";
    }

    public static boolean isSessionValid(Http.Context context) {
        return getSessionVersion().equals(context.session().get("version"));
    }
}
