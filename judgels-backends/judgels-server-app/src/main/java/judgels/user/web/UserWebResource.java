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
import judgels.api.setting.Settings;
import judgels.api.user.role.UserRole;
import judgels.api.user.web.UserWebConfig;
import judgels.profile.ProfileStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.setting.SettingStore;
import judgels.user.role.UserRoleStore;

@Path("/api/v2/user-web")
public class UserWebResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleStore roleStore;
    @Inject protected ProfileStore profileStore;
    @Inject protected WebConfiguration webConfig;
    @Inject protected SettingStore settingStore;

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
                .homeBanner(settings.getHome().getBanner())
                .announcements(webConfig.getAnnouncements());

        if (!authHeader.isPresent()) {
            config.role(new UserRole.Builder().build());
        } else {
            String actorJid = actorChecker.check(authHeader.get());
            config
                    .role(roleStore.getRole(actorJid))
                    .profile(profileStore.getProfile(actorJid));
        }
        return config.build();
    }
}
