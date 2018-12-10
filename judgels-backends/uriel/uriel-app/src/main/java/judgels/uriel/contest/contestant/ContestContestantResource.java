package judgels.uriel.contest.contestant;

import static com.google.common.base.Preconditions.checkArgument;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.contestant.ApprovedContestContestantsResponse;
import judgels.uriel.api.contest.contestant.ContestContestant;
import judgels.uriel.api.contest.contestant.ContestContestantConfig;
import judgels.uriel.api.contest.contestant.ContestContestantDeleteResponse;
import judgels.uriel.api.contest.contestant.ContestContestantService;
import judgels.uriel.api.contest.contestant.ContestContestantState;
import judgels.uriel.api.contest.contestant.ContestContestantUpsertResponse;
import judgels.uriel.api.contest.contestant.ContestContestantsResponse;
import judgels.uriel.contest.ContestStore;

public class ContestContestantResource implements ContestContestantService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestContestantRoleChecker contestantRoleChecker;
    private final ContestContestantStore contestantStore;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public ContestContestantResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestContestantRoleChecker contestantRoleChecker,
            ContestContestantStore contestantStore,
            UserSearchService userSearchService,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestantRoleChecker = contestantRoleChecker;
        this.contestantStore = contestantStore;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
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
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        boolean canManage = contestantRoleChecker.canManage(actorJid, contest);
        ContestContestantConfig config = new ContestContestantConfig.Builder()
                .canManage(canManage)
                .build();

        return new ContestContestantsResponse.Builder()
                .data(contestants)
                .profilesMap(profilesMap)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ApprovedContestContestantsResponse getApprovedContestants(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewApproved(actorJid, contest));

        Set<String> userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

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
        checkAllowed(contestantRoleChecker.canRegister(actorJid, contest));

        contestantStore.upsertContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork
    public void unregisterMyselfAsContestant(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canUnregister(actorJid, contest));

        contestantStore.deleteContestant(contestJid, actorJid);
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestContestantState getMyContestantState(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        return contestantRoleChecker.getContestantState(actorJid, contest);
    }

    @Override
    @UnitOfWork
    public ContestContestantUpsertResponse upsertContestants(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

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

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> insertedContestantProfilesMap = insertedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyContestantProfilesMap = alreadyContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestContestantUpsertResponse.Builder()
                .insertedContestantProfilesMap(insertedContestantProfilesMap)
                .alreadyContestantProfilesMap(alreadyContestantProfilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestContestantDeleteResponse deleteContestants(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedContestantUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (contestantStore.deleteContestant(contest.getJid(), userJid)) {
                deletedContestantUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> deletedContestantProfilesMap = deletedContestantUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestContestantDeleteResponse.Builder()
                .deletedContestantProfilesMap(deletedContestantProfilesMap)
                .build();
    }
}
