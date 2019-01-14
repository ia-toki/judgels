package org.iatoki.judgels.jophiel;

import judgels.jophiel.api.profile.BasicProfile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.api.actor.AuthHeader;
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
    private final MyUserService myUserService;
    private final ProfileService profileService;
    private final BaseUserService userService;

    @Inject
    public JophielClientController(JophielAuthAPI jophielAuthAPI, MyUserService myUserService, ProfileService profileService, BaseUserService userService) {
        this.jophielAuthAPI = jophielAuthAPI;
        this.myUserService = myUserService;
        this.profileService = profileService;
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
        User user = myUserService.getMyself(AuthHeader.of(session.getToken()));
        BasicProfile profile = profileService.getBasicProfile(session.getUserJid());

        if (profile.getName().isPresent()) {
            session("name", profile.getName().get());
        }

        session("username", user.getUsername());
        session("avatar", JophielClientControllerUtils.getInstance().getUserAvatarUrl(user.getJid()));
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
