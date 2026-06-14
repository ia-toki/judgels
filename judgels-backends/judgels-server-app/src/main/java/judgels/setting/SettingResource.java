package judgels.setting;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import judgels.api.setting.AppSettingsUpdateData;
import judgels.api.setting.Settings;
import judgels.role.SuperadminRoleStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/settings")
public class SettingResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected SuperadminRoleStore superadminRoleStore;
    @Inject protected SettingStore store;

    @Inject public SettingResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Settings getSettings(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(superadminRoleStore.isSuperadmin(actorJid));

        return store.getSettings();
    }

    @PUT
    @Path("/app")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Settings updateAppSettings(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            AppSettingsUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(superadminRoleStore.isSuperadmin(actorJid));

        store.updateAppSettings(data);

        return store.getSettings();
    }
}
