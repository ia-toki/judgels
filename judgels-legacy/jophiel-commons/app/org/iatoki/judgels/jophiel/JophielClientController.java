package org.iatoki.judgels.jophiel;

import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.api.jophiel.JophielUserProfile;
import org.iatoki.judgels.jophiel.user.BaseUserService;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

@Singleton
public final class JophielClientController extends Controller {

    private final JophielAuthAPI jophielAuthAPI;
    private final JophielPublicAPI jophielPublicAPI;
    private final BaseUserService userService;

    @Inject
    public JophielClientController(JophielAuthAPI jophielAuthAPI, JophielPublicAPI jophielPublicAPI, BaseUserService userService) {
        this.jophielAuthAPI = jophielAuthAPI;
        this.jophielPublicAPI = jophielPublicAPI;
        this.userService = userService;
    }

    public Result login(String returnUri) {
        return redirect(jophielAuthAPI.getAuthRequestUri(getRedirectUri().toString(), returnUri));
    }

    @Transactional
    public Result postLogin(String authCode, String returnUri) {
        JophielSession session = jophielAuthAPI.postLogin(authCode);

        session("userJid", session.getUserJid());
        session("token", session.getToken());
        session("version", JophielSessionUtils.getSessionVersion());

        userService.upsertUser(session.getUserJid(), session.getToken(), "unused", "unused", 0);

        refreshUserInfo(session);

        return redirect(returnUri);
    }

    public Result profile() {
        return redirect(JophielClientControllerUtils.getInstance().getUserEditProfileUrl());
    }

    public Result logout(String returnUri) {
        session().clear();
        return redirect(JophielClientControllerUtils.getInstance().getServiceLogoutUrl(returnUri));
    }

    private void refreshUserInfo(JophielSession session) {
        jophielPublicAPI.useOnBehalfOfUser(session.getToken());

        JophielUser user = jophielPublicAPI.findMyself();
        JophielUserProfile profile = jophielPublicAPI.findUserProfileByJid(session.getUserJid());

        if (profile.getName() != null) {
            session("name", profile.getName());
        }

        session("username", user.getUsername());

        if (user.getProfilePictureUrl() == null) {
            session("avatar", JophielClientControllerUtils.getInstance().getUserDefaultAvatarUrl());
        } else {
            session("avatar", user.getProfilePictureUrl());
        }
    }

    private URI getRedirectUri() {
        try {
            String uri = routes.JophielClientController.postLogin("", "").absoluteURL(request(), request().secure());
            String baseUri = uri.substring(0, uri.length() - 2);
            return new URI(baseUri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
