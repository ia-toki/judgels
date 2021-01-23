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
}
