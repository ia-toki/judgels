package judgels.uriel.contest.supervisor;

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
import judgels.uriel.api.contest.supervisor.ContestSupervisor;
import judgels.uriel.api.contest.supervisor.ContestSupervisorService;
import judgels.uriel.api.contest.supervisor.ContestSupervisorUpsertData;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsDeleteResponse;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsResponse;
import judgels.uriel.api.contest.supervisor.ContestSupervisorsUpsertResponse;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;

public class ContestSupervisorResource implements ContestSupervisorService {
    private final ActorChecker actorChecker;
    private final ContestStore contestStore;
    private final ContestLogger contestLogger;
    private final ContestRoleChecker roleChecker;
    private final ContestSupervisorStore supervisorStore;
    private final UserClient userClient;

    @Inject
    public ContestSupervisorResource(
            ActorChecker actorChecker,
            ContestStore contestStore,
            ContestLogger contestLogger,
            ContestRoleChecker roleChecker,
            ContestSupervisorStore supervisorStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestStore = contestStore;
        this.contestLogger = contestLogger;
        this.roleChecker = roleChecker;
        this.supervisorStore = supervisorStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestSupervisorsResponse getSupervisors(AuthHeader authHeader, String contestJid, Optional<Integer> page) {
        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canSupervise(actorJid, contest));

        Page<ContestSupervisor> supervisors = supervisorStore.getSupervisors(contestJid, page);
        Set<String> userJids =
                supervisors.getPage().stream().map(ContestSupervisor::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

        contestLogger.log(contestJid, "OPEN_SUPERVISORS");

        return new ContestSupervisorsResponse.Builder()
                .data(supervisors)
                .profilesMap(profilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestSupervisorsUpsertResponse upsertSupervisors(
            AuthHeader authHeader,
            String contestJid,
            ContestSupervisorUpsertData data) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canManage(actorJid, contest));

        checkArgument(data.getUsernames().size() <= 100, "Cannot add more than 100 users.");

        Map<String, String> usernameToJidMap = userClient.translateUsernamesToJids(data.getUsernames());

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> upsertedSupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            supervisorStore.upsertSupervisor(contest.getJid(), userJid, data.getManagementPermissions());
            upsertedSupervisorUsernames.add(username);
        });

        Map<String, Profile> userJidToProfileMap = userClient.getProfiles(userJids);
        Map<String, Profile> upsertedSupervisorProfilesMap = upsertedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "ADD_SUPERVISORS");

        return new ContestSupervisorsUpsertResponse.Builder()
                .upsertedSupervisorProfilesMap(upsertedSupervisorProfilesMap)
                .build();
    }

    @Override
    @UnitOfWork
    public ContestSupervisorsDeleteResponse deleteSupervisors(
            AuthHeader authHeader,
            String contestJid,
            Set<String> usernames) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canManage(actorJid, contest));

        checkArgument(usernames.size() <= 100, "Cannot remove more than 100 users.");

        Map<String, String> usernameToJidMap = userClient.translateUsernamesToJids(usernames);

        Set<String> userJids = ImmutableSet.copyOf(usernameToJidMap.values());
        Set<String> deletedSupervisorUsernames = Sets.newHashSet();
        usernameToJidMap.forEach((username, userJid) -> {
            if (supervisorStore.deleteSupervisor(contest.getJid(), userJid)) {
                deletedSupervisorUsernames.add(username);
            }
        });

        Map<String, Profile> userJidToProfileMap = userClient.getProfiles(userJids);
        Map<String, Profile> deletedSupervisorProfilesMap = deletedSupervisorUsernames
                .stream()
                .collect(Collectors.toMap(u -> u, u -> userJidToProfileMap.get(usernameToJidMap.get(u))));

        contestLogger.log(contestJid, "REMOVE_SUPERVISORS");

        return new ContestSupervisorsDeleteResponse.Builder()
                .deletedSupervisorProfilesMap(deletedSupervisorProfilesMap)
                .build();
    }
}
