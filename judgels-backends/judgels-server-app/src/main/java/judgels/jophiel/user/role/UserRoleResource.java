package judgels.jophiel.user.role;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
