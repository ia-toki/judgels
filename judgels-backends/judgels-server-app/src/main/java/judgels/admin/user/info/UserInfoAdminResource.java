package judgels.admin.user.info;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import judgels.api.user.User;
import judgels.api.user.info.UserInfo;
import judgels.service.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.user.UserRoleChecker;
import judgels.user.UserStore;
import judgels.user.info.UserInfoStore;

@Path("/api/v2/admin/users/{userJid}/info")
public class UserInfoAdminResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserInfoStore infoStore;

    @Inject public UserInfoAdminResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserInfo getInfo(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return infoStore.getInfo(user.getJid());
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UserInfo updateInfo(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid,
            UserInfo userInfo) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        User user = checkFound(userStore.getUserByJid(userJid));
        return infoStore.upsertInfo(user.getJid(), userInfo);
    }
}
