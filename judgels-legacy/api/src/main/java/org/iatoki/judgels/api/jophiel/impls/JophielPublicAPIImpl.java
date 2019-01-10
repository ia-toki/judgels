package org.iatoki.judgels.api.jophiel.impls;

import org.apache.http.HttpStatus;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.impls.AbstractJudgelsPublicAPIImpl;
import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.api.jophiel.JophielUserProfile;

public final class JophielPublicAPIImpl extends AbstractJudgelsPublicAPIImpl implements JophielPublicAPI {

    public JophielPublicAPIImpl(String baseUrl) {
        super(baseUrl, "/api/v2");
    }

    @Override
    public JophielUser findUserByJid(String userJid) {
        return sendGetRequest(interpolatePath("/users/:userJid", userJid)).asObjectFromJson(JophielUser.class);
    }

    @Override
    public JophielUser findMyself() {
        return sendGetRequest("/users/me").asObjectFromJson(JophielUser.class);
    }

    @Override
    public JophielUser findUserByUsername(String username) {
        try {
            return sendGetRequest(interpolatePath("/users/username/:username", username)).asObjectFromJson(JophielUser.class);
        } catch (JudgelsAPIClientException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    @Override
    public JophielUserProfile findUserProfileByJid(String userJid) {
        return sendGetRequest(interpolatePath("/users/:userJid/profile", userJid)).asObjectFromJson(JophielUserProfile.class);
    }

    @Override
    public String getUserAutocompleteAPIEndpoint() {
        return getEndpoint("/users/autocomplete");
    }
}
