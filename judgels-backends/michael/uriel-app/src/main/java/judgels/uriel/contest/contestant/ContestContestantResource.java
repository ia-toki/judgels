package judgels.uriel.contest.contestant;

import static com.google.common.base.Preconditions.checkArgument;
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
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ApprovedContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantConfig;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.contestant.ContestContestantsDeleteResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsUpsertResponse;
import judgels.uriel.api.contest.module.VirtualModuleConfig;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestContestantRoleChecker contestantRoleChecker;
    private final ContestContestantStore contestantStore;
    private final ContestModuleStore moduleStore;
    private final UserClient userClient;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestContestantRoleChecker contestantRoleChecker,
            ContestContestantStore contestantStore,
            ContestModuleStore moduleStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.contestantRoleChecker = contestantRoleChecker;
        this.contestantStore = contestantStore;
        this.moduleStore = moduleStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantsResponse getContestants(AuthHeader authHeader, String contestJid, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canSupervise(actorJid, contest));

        Page<ContestContestant> contestants = contestantStore.getContestants(contestJid, page);
        Set<String> userJids =
                contestants.getPage().stream().map(ContestContestant::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

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

    @Override
    @UnitOfWork(readOnly = true)
    public ApprovedContestContestantsResponse getApprovedContestants(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewApproved(actorJid, contest));

        Set<String> userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

        return new ApprovedContestContestantsResponse.Builder()
                .data(userJids)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public long getApprovedContestantsCount(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewApproved(actorJid, contest));

        return contestantStore.getApprovedContestantsCount(contestJid);
    }

    @Override
    @UnitOfWork
    public void registerMyselfAsContestant(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        Profile profile = userClient.getProfile(actorJid);
        checkAllowed(contestantRoleChecker.canRegister(actorJid, profile.getRating(), contest));

        contestLogger.log(contestJid, "REGISTER_CONTEST");

        contestantStore.upsertContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork
    public void unregisterMyselfAsContestant(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canUnregister(actorJid, contest));

        contestLogger.log(contestJid, "UNREGISTER_CONTEST");

        contestantStore.deleteContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantState getMyContestantState(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        Profile profile = userClient.getProfile(actorJid);

        return contestantRoleChecker.getContestantState(actorJid, profile.getRating(), contest);
    }

    @Override
    @UnitOfWork
    public ContestContestantsUpsertResponse upsertContestants(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 1000, "Cannot add more than 1000 users.");

        Map<String, String> usernameToJidMap = userClient.translateUsernamesToJids(usernames);

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

        Map<String, Profile> userJidToProfileMap = userClient.getProfiles(userJids);
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

    @Override
    @UnitOfWork
    public ContestContestantsDeleteResponse deleteContestants(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = userClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.deleteContestant(contest.getJid(), userJid)) {
                deletedContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = userClient.getProfiles(userJids);
        Map<String, Profile> deletedContestantProfilesMap = deletedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "REMOVE_CONTESTANTS");

        return new ContestContestantsDeleteResponse.Builder()
                .deletedContestantProfilesMap(deletedContestantProfilesMap)
                .build();
    }
}
