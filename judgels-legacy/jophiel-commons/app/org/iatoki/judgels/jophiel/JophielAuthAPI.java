package org.iatoki.judgels.jophiel;

import org.iatoki.judgels.api.impls.AbstractJudgelsClientAPIImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class JophielAuthAPI extends AbstractJudgelsClientAPIImpl {
    private final String raphaelBaseUrl;

    public JophielAuthAPI(String raphaelBaseUrl, String jophielBaseUrl, String clientJid, String clientSecret) {
        super(jophielBaseUrl, "/api/legacy", clientJid, clientSecret);
        this.raphaelBaseUrl = raphaelBaseUrl;
    }

    public String getAuthRequestUri(String redirectUri, String returnUri) {
        try {
            return raphaelBaseUrl + "/service-login/" + URLEncoder.encode(redirectUri, "UTF-8") + "/" + URLEncoder.encode(returnUri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public JophielSession postLogin(String authCode) {
        return sendPostRequest(interpolatePath("/session/post-login/:authCode", authCode))
                .asObjectFromJson(JophielSession.class);
    }
}
