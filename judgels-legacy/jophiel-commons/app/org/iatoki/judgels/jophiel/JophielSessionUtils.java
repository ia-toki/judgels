package org.iatoki.judgels.jophiel;

import play.mvc.Http;

public final class JophielSessionUtils {

    private JophielSessionUtils() {
        // prevent instantiation
    }

    public static String getSessionVersion() {
        return "2";
    }

    public static boolean isSessionValid(Http.Context context) {
        try {
            if (!context.session().containsKey("version") || !context.session().get("version").equals(getSessionVersion())) {
                return false;
            }

            if (context.request().method().equals("POST")) {
                return true;
            }

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
