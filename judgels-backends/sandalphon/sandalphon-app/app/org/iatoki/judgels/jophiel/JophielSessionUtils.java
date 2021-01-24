package org.iatoki.judgels.jophiel;

import java.util.Arrays;
import play.mvc.Http;

public final class JophielSessionUtils {
    private JophielSessionUtils() {}

    public static String getUserJid(Http.Request req) {
        return req.session().getOptional("userJid").orElse(null);
    }

    public static String getUsername(Http.Request req) {
        return req.session().getOptional("username").orElse(null);
    }

    public static String getSessionVersion() {
        return "4";
    }

    public static boolean hasRole(Http.Request req, String role) {
        return Arrays.asList(req.session().getOptional("role").orElse("").split(",")).contains(role);
    }

    public static boolean isSessionValid(Http.Request req) {
        return getSessionVersion().equals(req.session().getOptional("version").orElse(""));
    }
}
