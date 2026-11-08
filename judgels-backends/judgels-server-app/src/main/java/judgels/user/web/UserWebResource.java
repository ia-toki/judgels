package judgels.user.web;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.Optional;
import java.util.Set;
import judgels.api.setting.Settings;
import judgels.api.user.web.UserWebConfig;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.setting.SettingStore;
import judgels.user.account.UserPasswordResetter;
import judgels.user.registration.UserRegisterer;
import judgels.user.role.UserRoleStore;

@Path("/api/v2/user-web")
public class UserWebResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleStore roleStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected SettingStore settingStore;
    @Inject protected UserRegisterer userRegisterer;
    @Inject protected UserPasswordResetter userPasswordResetter;

    @Inject public UserWebResource() {}

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserWebConfig getWebConfig(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {
        Settings settings = settingStore.getSettings();
        UserWebConfig.Builder config = new UserWebConfig.Builder()
                .appName(settings.getApp().getName())
                .appSlogan(settings.getApp().getSlogan())
                .appAnnouncement(settings.getApp().getAnnouncement())
                .homeBanner(settings.getHome().getBanner())
                .userRegistrationEnabled(userRegisterer.isEnabled())
                .userResetPasswordEnabled(userPasswordResetter.isEnabled());

        if (!authHeader.isPresent()) {
            config.roles(Set.of());
        } else {
            String actorJid = actorChecker.check(authHeader.get());
            config
                    .roles(roleStore.getRoles(actorJid))
                    .profile(profileStore.getProfile(actorJid));
        }
        return config.build();
    }
}
