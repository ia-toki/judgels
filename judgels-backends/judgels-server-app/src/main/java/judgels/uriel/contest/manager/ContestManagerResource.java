package judgels.uriel.contest.manager;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.HashSet;
import java.util.Map;
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
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.api.contest.manager.ContestManagerConfig;
import judgels.uriel.api.contest.manager.ContestManagersDeleteResponse;
import judgels.uriel.api.contest.manager.ContestManagersResponse;
import judgels.uriel.api.contest.manager.ContestManagersUpsertResponse;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

@Path("/api/v2/contests/{contestJid}/managers")
public class ContestManagerResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestManagerRoleChecker managerRoleChecker;
    @Inject protected ContestManagerStore managerStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestManagerResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestManagersResponse getManagers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canView(actorJid, contest));

        Page<ContestManager> managers = managerStore.getManagers(contestJid, pageNumber, PAGE_SIZE);

        var userJids = Lists.transform(managers.getPage(), ContestManager::getUserJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids, contest.getBeginTime());

        boolean canManage = managerRoleChecker.canManage(actorJid);
        ContestManagerConfig config = new ContestManagerConfig.Builder()
                .canManage(canManage)
                .build();

        contestLogger.log(contestJid, "OPEN_MANAGERS");

        return new ContestManagersResponse.Builder()
                .data(managers)
                .profilesMap(profilesMap)
                .config(config)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestManagersUpsertResponse upsertManagers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> insertedManagerUsernames = new HashSet<>();
        Set<String> alreadyManagerUsernames = new HashSet<>();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.upsertManager(contest.getJid(), userJid)) {
                insertedManagerUsernames.add(username);
            } else {
                alreadyManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> insertedManagerProfilesMap = insertedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyManagerProfilesMap = alreadyManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "ADD_MANAGERS");

        return new ContestManagersUpsertResponse.Builder()
                .insertedManagerProfilesMap(insertedManagerProfilesMap)
                .alreadyManagerProfilesMap(alreadyManagerProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestManagersDeleteResponse deleteManagers(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> deletedManagerUsernames = new HashSet<>();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.deleteManager(contest.getJid(), userJid)) {
                deletedManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(usernameToJidMap.values());
        Map<String, Profile> deletedManagerProfilesMap = deletedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "REMOVE_MANAGERS");

        return new ContestManagersDeleteResponse.Builder()
                .deletedManagerProfilesMap(deletedManagerProfilesMap)
                .build();
    }
}
