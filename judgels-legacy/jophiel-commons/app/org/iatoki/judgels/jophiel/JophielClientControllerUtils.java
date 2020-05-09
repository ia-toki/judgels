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
        return raphaelBaseUrl + "/account";
    }

    public String getRegisterUrl() {
        return raphaelBaseUrl + "/register";
    }

    public String getUserIsLoggedInAPIEndpoint() {
        return jophielBaseUrl + "/api/play/session/is-logged-in";
    }

    public String getUserAvatarUrl(String userJid) {
        return jophielBaseUrl + "/api/v2/users/" + userJid + "/avatar";
    }

    public String getUserAutocompleteAPIEndpoint() {
        return jophielBaseUrl + "/api/v2/users/autocomplete";
    }

    public String getServiceLoginUrl(String authCode, String returnUri) {
        if (returnUri == null || returnUri.isEmpty()) {
            returnUri = raphaelBaseUrl;
        }
        try {
            return jophielBaseUrl + "/api/play/session/client-login/" + URLEncoder.encode(authCode, "UTF-8") + "/" + URLEncoder.encode(returnUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getServiceLogoutUrl(String returnUri) {
        if (returnUri == null || returnUri.isEmpty()) {
            returnUri = raphaelBaseUrl;
        }
        try {
            return jophielBaseUrl + "/api/play/session/client-logout/" + URLEncoder.encode(returnUri, "UTF-8");
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
