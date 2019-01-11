package org.iatoki.judgels.jophiel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class JophielClientControllerUtils {

    private static JophielClientControllerUtils INSTANCE;

    private final String raphaelBaseUrl;
    private final String jophielBaseUrl;

    private JophielClientControllerUtils(String raphaelBaseUrl, String jophielBaseUrl) {
        this.raphaelBaseUrl = raphaelBaseUrl;
        this.jophielBaseUrl = jophielBaseUrl;
    }

    public String getUserEditProfileUrl() {
        return raphaelBaseUrl + "/account/profile";
    }

    public String getRegisterUrl() {
        return raphaelBaseUrl + "/register";
    }

    public String getUserAvatarUrl(String userJid) {
        return jophielBaseUrl + "/api/v2/users/" + userJid + "/avatar";
    }

    public String getServiceLogoutUrl(String returnUri) {
        if (returnUri == null || returnUri.isEmpty()) {
            returnUri = raphaelBaseUrl;
        }
        try {
            return raphaelBaseUrl + "/service-logout/" + URLEncoder.encode(returnUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void buildInstance(String raphaelBaseUrl, String jophielBaseUrl) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("JophielClientControllerUtils instance has already been built");
        }
        INSTANCE = new JophielClientControllerUtils(raphaelBaseUrl, jophielBaseUrl);
    }

    public static JophielClientControllerUtils getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("JophielClientControllerUtils instance has not been built");
        }
        return INSTANCE;
    }
}
