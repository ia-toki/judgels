package judgels.uriel.contest.contestant;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import judgels.uriel.api.contest.contestant.ApprovedContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantConfig;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.contestant.ContestContestantsDeleteResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsUpsertResponse;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

@Path("/api/v2/contests/{contestJid}/contestants")
public class ContestContestantResource {
    private static final int PAGE_SIZE = 1000;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestContestantRoleChecker contestantRoleChecker;
    @Inject protected ContestContestantStore contestantStore;
    @Inject protected ContestModuleStore moduleStore;
    @Inject protected JophielClient jophielClient;

    @Inject public ContestContestantResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestContestantsResponse getContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canSupervise(actorJid, contest));

        Page<ContestContestant> contestants = contestantStore.getContestants(contestJid, pageNumber, PAGE_SIZE);
        Set<String> userJids =
                contestants.getPage().stream().map(ContestContestant::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids, contest.getBeginTime());

        boolean canManage = contestantRoleChecker.canManage(actorJid, contest);
        ContestContestantConfig config = new ContestContestantConfig.Builder()
                .canManage(canManage)
                .build();
        Optional<VirtualModuleConfig> virtualModuleConfig = moduleStore.getVirtualModuleConfig(contestJid);

        contestLogger.log(contestJid, "OPEN_CONTESTANTS");

        return new ContestContestantsResponse.Builder()
                .data(contestants)
                .profilesMap(profilesMap)
                .config(config)
                .virtualModuleConfig(virtualModuleConfig)
                .build();
    }

    @GET
    @Path("/approved")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ApprovedContestContestantsResponse getApprovedContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewApproved(actorJid, contest));

        Set<String> userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, Profile> profilesMap = jophielClient.getProfiles(userJids, contest.getBeginTime());

        return new ApprovedContestContestantsResponse.Builder()
                .data(userJids)
                .profilesMap(profilesMap)
                .build();
    }

    @GET
    @Path("/approved/count")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public long getApprovedContestantsCount(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewApproved(actorJid, contest));

        return contestantStore.getApprovedContestantsCount(contestJid);
    }

    @POST
    @Path("/me")
    @UnitOfWork
    public void registerMyselfAsContestant(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        Profile profile = jophielClient.getProfile(actorJid);
        checkAllowed(contestantRoleChecker.canRegister(actorJid, profile.getRating(), contest));

        contestLogger.log(contestJid, "REGISTER_CONTEST");

        contestantStore.upsertContestant(contestJid, actorJid);
    }

    @DELETE
    @Path("/me")
    @UnitOfWork
    public void unregisterMyselfAsContestant(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canUnregister(actorJid, contest));

        contestLogger.log(contestJid, "UNREGISTER_CONTEST");

        contestantStore.deleteContestant(contestJid, actorJid);
    }

    @GET
    @Path("/me/state")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestContestantState getMyContestantState(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        Profile profile = jophielClient.getProfile(actorJid);

        return contestantRoleChecker.getContestantState(actorJid, profile.getRating(), contest);
    }

    @POST
    @Path("/batch-upsert")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestContestantsUpsertResponse upsertContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 1000, "Cannot add more than 1000 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> insertedContestantUsernames = Sets.newHashSet();
        Set<String> alreadyContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.upsertContestant(contest.getJid(), userJid)) {
                insertedContestantUsernames.add(username);
            } else {
                alreadyContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(userJids);
        Map<String, Profile> insertedContestantProfilesMap = insertedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyContestantProfilesMap = alreadyContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "ADD_CONTESTANTS");

        return new ContestContestantsUpsertResponse.Builder()
                .insertedContestantProfilesMap(insertedContestantProfilesMap)
                .alreadyContestantProfilesMap(alreadyContestantProfilesMap)
                .build();
    }

    @POST
    @Path("/batch-delete")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public ContestContestantsDeleteResponse deleteContestants(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("contestJid") String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = jophielClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.deleteContestant(contest.getJid(), userJid)) {
                deletedContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = jophielClient.getProfiles(userJids);
        Map<String, Profile> deletedContestantProfilesMap = deletedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "REMOVE_CONTESTANTS");

        return new ContestContestantsDeleteResponse.Builder()
                .deletedContestantProfilesMap(deletedContestantProfilesMap)
                .build();
    }
}
