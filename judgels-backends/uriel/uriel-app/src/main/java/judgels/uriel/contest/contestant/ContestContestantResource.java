package judgels.uriel.contest.contestant;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
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
    public ContestContestantsResponse getApprovedContestants(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewList(actorJid, contest));

        Set<String> userJids = contestantStore.getApprovedContestantJids(contestJid);
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids, contest.getBeginTime());

        return new ContestContestantsResponse.Builder()
                .data(userJids)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public long getApprovedContestantsCount(AuthHeader authHeader, String contestJid) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(contestantRoleChecker.canViewList(actorJid, contest));

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

        contestantStore.removeContestant(contestJid, actorJid);
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
        checkAllowed(contestantRoleChecker.canSupervise(actorJid, contest));

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
}
