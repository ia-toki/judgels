package judgels.admin.user;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import judgels.api.user.User;
import judgels.api.user.UserData;
import judgels.api.user.UsersResponse;
import judgels.api.user.UsersUpsertResponse;
import judgels.persistence.actor.ActorChecker;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.api.actor.AuthHeader;
import judgels.session.SessionStore;
import judgels.user.UserCreator;
import judgels.user.UserRoleChecker;
import judgels.user.UserStore;

@Path("/api/v2/admin/users")
public class UserAdminResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected SessionStore sessionStore;
    @Inject protected UserCreator userCreator;

    @Inject public UserAdminResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UsersResponse getUsers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("orderBy") Optional<String> orderBy,
            @QueryParam("orderDir") Optional<OrderDir> orderDir,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        Page<User> users = userStore.getUsers(pageNumber, PAGE_SIZE, orderBy, orderDir);

        var userJids = Lists.transform(users.getPage(), User::getJid);
        Map<String, Instant> lastSessionTimesMap = sessionStore.getLatestSessionTimeByUserJids(userJids);
        return new UsersResponse.Builder()
                .data(users)
                .lastSessionTimesMap(lastSessionTimesMap)
                .build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public User createUser(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            UserData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.createUser(data);
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(TEXT_PLAIN)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UsersUpsertResponse upsertUsers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            String csv) throws IOException {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        UserCreator.UpsertUsersResult result = userCreator.upsertUsers(csv);
        if (result.errorMessage.isPresent()) {
            throw new IllegalArgumentException(result.errorMessage.get());
        }

        return new UsersUpsertResponse.Builder()
                .createdUsernames(result.createdUsernames)
                .updatedUsernames(result.updatedUsernames)
                .build();
    }

    @GET
    @Path("/username/{username}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public User getUserByUsername(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("username") String username) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return checkFound(userStore.getUserByUsername(username));
    }
}
