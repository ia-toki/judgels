package judgels.jophiel.user.me;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import judgels.jophiel.api.role.UserRole;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.me.PasswordUpdateData;
import judgels.jophiel.role.UserRoleStore;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users/me")
public class MyUserResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleStore roleStore;
    @Inject protected UserStore userStore;

    @Inject public MyUserResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public User getMyself(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return checkFound(userStore.getUserByJid(actorJid));
    }

    @POST
    @Path("/password")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateMyPassword(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            PasswordUpdateData data) {

        String actorJid = actorChecker.check(authHeader);

        userStore.validateUserPassword(actorJid, data.getOldPassword());
        userStore.updateUserPassword(actorJid, data.getNewPassword());
    }

    @GET
    @Path("/role")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserRole getMyRole(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return roleStore.getRole(actorJid);
    }
}
