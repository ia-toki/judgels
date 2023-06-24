package judgels.uriel.contest.supervisor;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
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
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsDeleteResponse;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsResponse;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsUpsertResponse;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

@Path("/api/v2/contests/{contestJid}/supervisors")
public class ContestSupervisorResource {
    private static final int PAGE_SIZE = 250;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestRoleChecker roleChecker;
    @Inject protected ContestSupervisorStore supervisorStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestSupervisorResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestSupervisorsResponse getSupervisors(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canSupervise(actorJid, contest));

        Page<ContestSupervisor> supervisors = supervisorStore.getSupervisors(contestJid, pageNumber, PAGE_SIZE);
        Set<String> userJids =
                supervisors.getPage().stream().map(ContestSupervisor::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids, contest.getBeginTime());

        contestLogger.log(contestJid, "OPEN_SUPERVISORS");

        return new ContestSupervisorsResponse.Builder()
                .data(supervisors)
                .profilesMap(profilesMap)
                .build();
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestSupervisorsUpsertResponse upsertSupervisors(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            ContestSupervisorUpsertData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canManage(actorJid, contest));

        checkArgument(data.getUsernames().size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(data.getUsernames());

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> upsertedSupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            supervisorStore.upsertSupervisor(contest.getJid(), userJid, data.getManagementPermissions());
            upsertedSupervisorUsernames.add(username);
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(userJids);
        Map<String, Profile> upsertedSupervisorProfilesMap = upsertedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "ADD_SUPERVISORS");

        return new ContestSupervisorsUpsertResponse.Builder()
                .upsertedSupervisorProfilesMap(upsertedSupervisorProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestSupervisorsDeleteResponse deleteSupervisors(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedSupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (supervisorStore.deleteSupervisor(contest.getJid(), userJid)) {
                deletedSupervisorUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(userJids);
        Map<String, Profile> deletedSupervisorProfilesMap = deletedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "REMOVE_SUPERVISORS");

        return new ContestSupervisorsDeleteResponse.Builder()
                .deletedSupervisorProfilesMap(deletedSupervisorProfilesMap)
                .build();
    }
}
