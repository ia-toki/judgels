package judgels.jophiel.user;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UsersResponse;
import judgels.jophiel.api.user.UsersUpsertResponse;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import liquibase.util.csv.CSVReader;
import liquibase.util.csv.CSVWriter;

@Path("/api/v2/users")
public class UserResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserInfoStore infoStore;
    @Inject protected SessionStore sessionStore;

    @Inject public UserResource() {}

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
        Set<String> userJids = users.getPage().stream().map(User::getJid).collect(Collectors.toSet());
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
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            List<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        Map<String, User> usersMap = userStore.getUsersByUsername(ImmutableSet.copyOf(usernames));
        List<User> users = usernames.stream()
                .filter(usersMap::containsKey)
                .map(usersMap::get)
                .collect(Collectors.toList());

        StringWriter csv = new StringWriter();
        CSVWriter writer = new CSVWriter(csv);

        writer.writeNext(new String[]{"jid", "username", "email"}, false);
        for (User user : users) {
            writer.writeNext(new String[]{user.getJid(), user.getUsername(), user.getEmail()}, false);
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

        CSVReader reader = new CSVReader(new StringReader(csv));

        String[] header = reader.readNext();
        Map<String, Integer> headerMap = Maps.newHashMap();
        for (int i = 0; i < header.length; i++) {
            headerMap.put(header[i], i);
        }

        ImmutableList.Builder<String> createdUsernames = ImmutableList.builder();
        ImmutableList.Builder<String> updatedUsernames = ImmutableList.builder();
        while (true) {
            String[] line = reader.readNext();
            if (line == null) {
                break;
            }

            String username = line[headerMap.get("username")];

            User user;
            Optional<User> existingUser;
            UserInfo existingInfo;
            Optional<String> jid = getCsvValue(headerMap, line, "jid");
            if (jid.isPresent()) {
                existingUser = userStore.getUserByJid(jid.get());
            } else {
                existingUser = userStore.getUserByUsername(username);
            }

            if (existingUser.isPresent()) {
                UserUpdateData data = new UserUpdateData.Builder()
                        .username(username)
                        .password(getCsvValue(headerMap, line, "password"))
                        .email(getCsvValue(headerMap, line, "email"))
                        .build();

                user = userStore.updateUser(existingUser.get().getJid(), data);
                updatedUsernames.add(username);
                existingInfo = infoStore.getInfo(user.getJid());
            } else {
                UserData data = new UserData.Builder()
                        .username(username)
                        .password(getCsvValue(headerMap, line, "password"))
                        .email(line[headerMap.get("email")])
                        .build();

                user = jid.isPresent()
                        ? userStore.createUserWithJid(jid.get(), data)
                        : userStore.createUser(data);
                createdUsernames.add(username);
                existingInfo = new UserInfo.Builder().build();
            }

            UserInfo.Builder info = new UserInfo.Builder().from(existingInfo);
            getCsvValue(headerMap, line, "name").ifPresent(info::name);
            getCsvValue(headerMap, line, "country").ifPresent(info::country);
            infoStore.upsertInfo(user.getJid(), info.build());

            if (headerMap.containsKey("password")) {
                sessionStore.deleteSessionsByUserJid(user.getJid());
            }
        }

        return new UsersUpsertResponse.Builder()
                .createdUsernames(createdUsernames.build())
                .updatedUsernames(updatedUsernames.build())
                .build();
    }

    private Optional<String> getCsvValue(Map<String, Integer> headerMap, String[] line, String key) {
        if (!headerMap.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(line[headerMap.get(key)]);
    }
}
