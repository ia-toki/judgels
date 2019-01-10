package org.iatoki.judgels.api.jophiel.impls;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.iatoki.judgels.api.impls.AbstractJudgelsClientAPIImpl;
import org.iatoki.judgels.api.jophiel.JophielClientAPI;
import org.iatoki.judgels.api.jophiel.JophielUserActivityMessage;

import java.util.List;

public final class JophielClientAPIImpl extends AbstractJudgelsClientAPIImpl implements JophielClientAPI {

    private final String baseUrl;

    public JophielClientAPIImpl(String baseUrl, String clientJid, String clientSecret) {
        super(baseUrl, clientJid, clientSecret);
        this.baseUrl = baseUrl;
    }

    @Override
    public void sendUserActivityMessages(List<JophielUserActivityMessage> activityMessages) {
        JsonElement requestBody = new Gson().toJsonTree(activityMessages);
        sendPostRequest("/activities", requestBody);
    }

    @Override
    public String getUserEditProfileEndpoint() {
        return baseUrl + "/profile";
    }

    @Override
    public String getUserSearchProfileEndpoint() {
        return baseUrl + "/profiles/search";
    }

    @Override
    public String getRegisterEndpoint() {
        return baseUrl + "/register";
    }

    @Override
    public String getUserIsLoggedInAPIEndpoint() {
        return baseUrl + "/api/legacy/session/is-logged-in";
    }

    @Override
    public String getLinkedClientsAPIEndpoint() {
        return getEndpoint("/clients/linked");
    }
}
