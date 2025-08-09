package judgels.jophiel.user.role;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.user.role.UserRole;
import judgels.jophiel.api.user.role.UserRolesResponse;
import judgels.jophiel.api.user.role.UserWithRole;
import judgels.jophiel.profile.ProfileStore;
import judgels.jophiel.user.UserRoleChecker;
import judgels.jophiel.user.UserStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/user-roles")
public class UserRoleResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected UserRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected UserRoleStore userRoleStore;
    @Inject protected ProfileStore profileStore;

    @Inject public UserRoleResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public UserRolesResponse getUserRoles(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
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

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void setUserRoles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            Map<String, UserRole> usernameToRoleMap) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.canAdminister(actorJid));

        var userJids = usernameToRoleMap.keySet();
        Map<String, String> usernameToJidMap = userStore.translateUsernamesToJids(userJids);

        Map<String, UserRole> userJidToRoleMap = new HashMap<>();
        for (var entry : usernameToRoleMap.entrySet()) {
            if (usernameToJidMap.containsKey(entry.getKey())) {
                userJidToRoleMap.put(usernameToJidMap.get(entry.getKey()), entry.getValue());
            }
        }

        userRoleStore.setRoles(userJidToRoleMap);
    }
}
