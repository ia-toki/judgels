package org.iatoki.judgels.jophiel;

import org.iatoki.judgels.jophiel.avatar.AbstractBaseAvatarCacheServiceImpl;
import org.iatoki.judgels.play.IdentityUtils;
import play.mvc.Http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class JophielClientControllerUtils {

    private static JophielClientControllerUtils INSTANCE;

    private final String raphaelBaseUrl;
    private final String baseUrl;

    private JophielClientControllerUtils(String raphaelBaseUrl, String baseUrl) {
        this.raphaelBaseUrl = raphaelBaseUrl;
        this.baseUrl = baseUrl;
    }

    public String getUserEditProfileUrl() {
        return raphaelBaseUrl + "/account/profile";
    }

    public String getUserSearchProfileUrl() {
        return baseUrl + "/profiles/search";
    }

    public String getRegisterUrl() {
        return raphaelBaseUrl + "/register";
    }

    public String getUserDefaultAvatarUrl() {
        return raphaelBaseUrl + "/avatar-default.png";
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

    public static void buildInstance(String raphaelBaseUrl, String baseUrl) {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("JophielClientControllerUtils instance has already been built");
        }
        INSTANCE = new JophielClientControllerUtils(raphaelBaseUrl, baseUrl);
    }

    public static JophielClientControllerUtils getInstance() {
        if (INSTANCE == null) {
            throw new UnsupportedOperationException("JophielClientControllerUtils instance has not been built");
        }
        return INSTANCE;
    }

    public static void updateUserAvatarCache(AbstractBaseAvatarCacheServiceImpl<?> avatarCacheService) {
        if (IdentityUtils.getUserJid() != null) {
            avatarCacheService.putImageUrl(IdentityUtils.getUserJid(), Http.Context.current().session().get("avatar"), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }
    }
}
