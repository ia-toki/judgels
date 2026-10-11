package judgels.user.role;

import static com.google.common.base.Preconditions.checkArgument;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkAllowed;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import judgels.api.profile.Profile;
import judgels.api.user.role.UserRole;
import judgels.api.user.role.UserRolesResponse;
import judgels.api.user.role.UserRolesUpsertData;
import judgels.api.user.role.UserRolesUpsertResponse;
import judgels.api.user.role.UserWithRole;
import judgels.core.api.AuthHeader;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;
import judgels.user.UserRoleChecker;
import judgels.user.UserStore;

@Path("/api/v2/user-roles")
public class UserRoleResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserRoleStore userRoleStore;
    @Inject protected SuperadminRoleStore superadminRoleStore;
    @Inject protected ProfileStore profileStore;

    @Inject public UserRoleResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserRolesResponse getRoles(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        List<UserWithRole> roles = userRoleStore.getRoles();

        var userJids = Lists.transform(roles, UserWithRole::getUserJid);
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        return new UserRolesResponse.Builder()
                .data(roles)
                .profilesMap(profilesMap)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public UserRolesUpsertResponse upsertRoles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            UserRolesUpsertData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        checkArgument(data.getUsernames().size() <= 100, "Cannot add more than 100 users.");
        checkRole(data.getRole());
        checkArgument(!data.getRole().equals(new UserRole.Builder().build()), "No role given.");

        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(data.getUsernames());
        for (String userJid : usernameToJidMap.values()) {
            checkAllowed(!superadminRoleStore.isSuperadmin(userJid));
        }
        for (String userJid : usernameToJidMap.values()) {
            userRoleStore.upsertRole(userJid, data.getRole());
        }

        Map<String, Profile> userJidToProfileMap = profileStore.getProfiles(usernameToJidMap.values());
        Map<String, Profile> upsertedUserProfilesMap = usernameToJidMap.keySet()
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new UserRolesUpsertResponse.Builder()
                .upsertedUserProfilesMap(upsertedUserProfilesMap)
                .build();
    }

    @PUT
    @Path("/{userJid}")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setRole(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("userJid") String userJid,
            UserRole role) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));
        checkFound(userStore.getUserByJid(userJid));

        checkRole(role);
        checkAllowed(!superadminRoleStore.isSuperadmin(userJid));
        // A system admin cannot lock themselves out.
        checkAllowed(!userJid.equals(actorJid) || role.getAccount().isPresent());

        userRoleStore.setRole(userJid, role);
    }

    private static void checkRole(UserRole role) {
        Stream.of(role.getAccount(), role.getProblem(), role.getContest(), role.getTraining())
                .flatMap(Optional::stream)
                .forEach(r -> checkArgument(r.equals("ADMIN"), "Unknown role: %s.", r));
    }
}
