package org.iatoki.judgels.sandalphon.controllers.api.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.apache.commons.codec.binary.Base32;

public final class TOTPUtils {

    private TOTPUtils() {
        // prevent instantiation
    }

    public static boolean match(String serverSecret, int clientTotpCode) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        return googleAuthenticator.authorize(new Base32().encodeAsString(serverSecret.getBytes()), clientTotpCode);
    }
}
