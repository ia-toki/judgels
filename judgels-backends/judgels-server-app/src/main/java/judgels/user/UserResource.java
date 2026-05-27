package judgels.user;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.user.User;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/users")
public class UserResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;

    @Inject public UserResource() {}

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
