package judgels.jophiel.user;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
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
import java.io.StringWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UsersResponse;
import judgels.jophiel.api.user.UsersUpsertResponse;
import judgels.jophiel.session.SessionStore;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public class UserResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected SessionStore sessionStore;
    @Inject protected UserCreator userCreator;

    @Inject public UserResource() {}

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
    @Path("/batch-get")
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    @UnitOfWork(readOnly = true)
    public String exportUsers(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            List<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        boolean canAdminister = roleChecker.canAdminister(actorJid);

        checkArgument(usernames.size() <= 100, "Cannot get more than 100 users.");

        Map<String, User> usersMap = userStore.getUsersByUsername(Set.copyOf(usernames));
        List<User> users = usernames.stream()
                .filter(usersMap::containsKey)
                .map(usersMap::get)
                .collect(Collectors.toList());

        StringWriter csv = new StringWriter();
        ICSVWriter writer = (new CSVWriterBuilder(csv)).build();

        List<String> header = new ArrayList<>();
        header.add("username");
        header.add("jid");
        if (canAdminister) {
            header.add("email");
        }

        writer.writeNext(header.toArray(new String[0]), false);
        for (User user : users) {
            List<String> row = new ArrayList<>();
            row.add(user.getUsername());
            row.add(user.getJid());
            if (canAdminister) {
                row.add(user.getEmail());
            }
            writer.writeNext(row.toArray(new String[0]), false);
        }
        return csv.toString();
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
    @Path("/{userJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public User getUser(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        return checkFound(userStore.getUserByJid(userJid));
    }

    @GET
    @Path("/me")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public User getMyself(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        return checkFound(userStore.getUserByJid(actorJid));
    }
}
