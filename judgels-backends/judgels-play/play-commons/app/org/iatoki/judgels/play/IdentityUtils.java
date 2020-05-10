package org.iatoki.judgels.play;

import play.mvc.Http;

/**
 * @deprecated Will be refactored out in the future.
 */
@Deprecated
public final class IdentityUtils {

    private IdentityUtils() {
        // prevent instantiation
    }

    public static String getUserJid() {
        return Http.Context.current().session().get("userJid");
    }

    public static String getUsername() {
        String username = Http.Context.current().session().get("username");

        return username;
    }

    public static String getUserRealName() {
        return Http.Context.current().session().get("name");
    }

    public static String getIpAddress() {
        return Http.Context.current().request().remoteAddress();
    }
}
