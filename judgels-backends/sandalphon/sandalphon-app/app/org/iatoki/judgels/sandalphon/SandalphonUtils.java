package org.iatoki.judgels.sandalphon;

import java.util.Arrays;
import play.mvc.Http;

public final class SandalphonUtils {

    private SandalphonUtils() {
        // prevent instantiation
    }

    public static boolean hasRole(String role) {
        return Arrays.asList(getFromSession("role").split(",")).contains(role);
    }

    private static String getFromSession(String key) {
        return Http.Context.current().session().get(key);
    }
}
