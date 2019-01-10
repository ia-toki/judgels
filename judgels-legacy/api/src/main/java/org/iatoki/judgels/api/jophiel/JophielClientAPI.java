package org.iatoki.judgels.api.jophiel;

import org.iatoki.judgels.api.JudgelsClientAPI;

import java.util.List;

public interface JophielClientAPI extends JudgelsClientAPI {

    @Deprecated
    void sendUserActivityMessages(List<JophielUserActivityMessage> activityMessages);

    String getUserEditProfileEndpoint();

    @Deprecated
    String getUserSearchProfileEndpoint();

    String getRegisterEndpoint();

    String getUserIsLoggedInAPIEndpoint();

    String getLinkedClientsAPIEndpoint();
}
