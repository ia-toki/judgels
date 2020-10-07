package judgels.jophiel.user;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jophiel.api.user.User;
import judgels.jophiel.api.user.UserData;
import judgels.jophiel.api.user.UserService;
import judgels.jophiel.api.user.UsersUpsertResponse;
import judgels.jophiel.api.user.dump.ExportUsersDumpData;
import judgels.jophiel.api.user.dump.UsersDump;
import judgels.jophiel.api.user.info.UserInfo;
import judgels.jophiel.session.SessionStore;
import judgels.jophiel.user.info.UserInfoStore;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import liquibase.util.csv.CSVReader;

public class UserResource implements UserService {
    private final ActorChecker actorChecker;
    private final UserRoleChecker roleChecker;
    private final UserStore userStore;
    private final UserInfoStore infoStore;
    private final SessionStore sessionStore;

    @Inject
    public UserResource(
            ActorChecker actorChecker,
            UserRoleChecker roleChecker,
            UserStore userStore,
            UserInfoStore infoStore,
            SessionStore sessionStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.userStore = userStore;
        this.infoStore = infoStore;
        this.sessionStore = sessionStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public User getUser(AuthHeader authHeader, String userJid) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canManage(actorJid, userJid));

        return checkFound(userStore.getUserByJid(userJid));
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Page<User> getUsers(
            AuthHeader authHeader,
            Optional<Integer> page,
            Optional<String> orderBy,
            Optional<OrderDir> orderDir) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.getUsers(page, orderBy, orderDir);
    }

    @Override
    @UnitOfWork
    public User createUser(AuthHeader authHeader, UserData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.createUser(data);
    }

    @Override
    @UnitOfWork
    public List<User> createUsers(AuthHeader authHeader, List<UserData> data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return userStore.createUsers(data);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public UsersDump exportUsers(AuthHeader authHeader, ExportUsersDumpData users) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        return new UsersDump.Builder()
                .users(ImmutableList.copyOf(
                        userStore.getUsersByUsername(ImmutableSet.copyOf(users.getUsernames())).values())
                )
                .build();
    }

    @Override
    @UnitOfWork
    public UsersUpsertResponse upsertUsers(AuthHeader authHeader, String csv) throws IOException {
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
            UserData userData = new UserData.Builder()
                    .username(username)
                    .password(line[headerMap.get("password")])
                    .email(line[headerMap.get("email")])
                    .build();

            Optional<User> existingUser;
            Optional<String> jid = getCsvValue(headerMap, line, "jid");
            if (jid.isPresent()) {
                existingUser = userStore.getUserByJid(jid.get());
            } else {
                existingUser = userStore.getUserByUsername(username);
            }

            if (existingUser.isPresent()) {
                user = userStore.updateUser(existingUser.get().getJid(), userData).get();
                updatedUsernames.add(username);
            } else {
                user = jid.isPresent()
                        ? userStore.createUserWithJid(jid.get(), userData)
                        : userStore.createUser(userData);
                createdUsernames.add(username);
            }

            infoStore.upsertInfo(user.getJid(), new UserInfo.Builder()
                    .name(getCsvValue(headerMap, line, "name"))
                    .country(getCsvValue(headerMap, line, "country"))
                    .build());
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
