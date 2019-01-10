package org.iatoki.judgels.api.jophiel;

import org.iatoki.judgels.api.JudgelsPublicAPI;

public interface JophielPublicAPI extends JudgelsPublicAPI {

    JophielUser findUserByJid(String userJid);

    JophielUser findMyself();

    JophielUser findUserByUsername(String username);

    JophielUserProfile findUserProfileByJid(String userJid);

    String getUserAutocompleteAPIEndpoint();
}
