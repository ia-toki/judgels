package judgels.uriel.contest.manager;

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
import judgels.uriel.api.contest.manager.ContestManager;
import judgels.uriel.api.contest.manager.ContestManagerConfig;
import judgels.uriel.api.contest.manager.ContestManagerService;
import judgels.uriel.api.contest.manager.ContestManagersDeleteResponse;
import judgels.uriel.api.contest.manager.ContestManagersResponse;
import judgels.uriel.api.contest.manager.ContestManagersUpsertResponse;
import judgels.uriel.contest.ContestStore;

public class ContestManagerResource implements ContestManagerService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestManagerRoleChecker managerRoleChecker;
    private final ContestManagerStore managerStore;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public ContestManagerResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestManagerRoleChecker managerRoleChecker,
            ContestManagerStore managerStore,
            UserSearchService userSearchService,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.managerRoleChecker = managerRoleChecker;
        this.managerStore = managerStore;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestManagersResponse getManagers(AuthHeader authHeader, String contestJid, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canView(actorJid, contest));

        Page<ContestManager> managers = managerStore.getManagers(contestJid, page);
        Set<String> userJids =
                managers.getPage().stream().map(ContestManager::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());
        boolean canManage = managerRoleChecker.canManage(actorJid);
        ContestManagerConfig config = new ContestManagerConfig.Builder()
                .canManage(canManage)
                .build();

        return new ContestManagersResponse.Builder()
                .data(managers)
                .profilesMap(profilesMap)
                .config(config)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestManagersUpsertResponse upsertManagers(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> insertedManagerUsernames = Sets.newHashSet();
        Set<String> alreadyManagerUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.upsertManager(contest.getJid(), userJid)) {
                insertedManagerUsernames.add(username);
            } else {
                alreadyManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> insertedManagerProfilesMap = insertedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));
        Map<String, Profile> alreadyManagerProfilesMap = alreadyManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestManagersUpsertResponse.Builder()
                .insertedManagerProfilesMap(insertedManagerProfilesMap)
                .alreadyManagerProfilesMap(alreadyManagerProfilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestManagersDeleteResponse deleteManagers(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(managerRoleChecker.canManage(actorJid));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedManagerUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (managerStore.deleteManager(contest.getJid(), userJid)) {
                deletedManagerUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = profileService.getProfiles(userJids);
        Map<String, Profile> deletedManagerProfilesMap = deletedManagerUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        return new ContestManagersDeleteResponse.Builder()
                .deletedManagerProfilesMap(deletedManagerProfilesMap)
                .build();
    }
}
