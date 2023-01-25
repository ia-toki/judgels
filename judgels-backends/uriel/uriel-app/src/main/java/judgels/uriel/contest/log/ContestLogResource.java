package judgels.uriel.contest.log;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.api.contest.log.ContestLogConfig;
import judgels.uriel.api.contest.log.ContestLogService;
import judgels.uriel.api.contest.log.ContestLogsResponse;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.contestant.ContestContestantStore;
import judgels.uriel.contest.problem.ContestProblemStore;
import judgels.uriel.contest.supervisor.ContestSupervisorStore;

public class ContestLogResource implements ContestLogService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestLogStore logStore;
    private final ContestContestantStore contestantStore;
    private final ContestSupervisorStore supervisorStore;
    private final ContestProblemStore problemStore;
    private final UserClient userClient;

    @Inject
    public ContestLogResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestLogStore logStore,
            ContestContestantStore contestantStore,
            ContestSupervisorStore supervisorStore,
            ContestProblemStore problemStore,
            UserClient userClient) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.logStore = logStore;
        this.contestantStore = contestantStore;
        this.supervisorStore = supervisorStore;
        this.problemStore = problemStore;
        this.userClient = userClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestLogsResponse getLogs(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> username,
            Optional<String> problemAlias,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        Optional<String> userJid = username.map(u -> userClient.translateUsernameToJid(u).orElse(""));
        Optional<String> problemJid = problemAlias.map(alias -> problemStore
                .getProblemByAlias(contestJid, alias)
                .map(ContestProblem::getProblemJid)
                .orElse(""));

        Page<ContestLog> logs = logStore.getLogs(contestJid, userJid, problemJid, page);

        Set<String> userJids = ImmutableSet.<String>builder()
                .addAll(Lists.transform(logs.getPage(), ContestLog::getUserJid))
                .addAll(contestantStore.getApprovedContestantJids(contestJid))
                .addAll(supervisorStore.getAllSupervisorJids(contestJid))
                .build();

        Map<String, Profile> profilesMap = userClient.getProfiles(userJids, contest.getBeginTime());

        List<String> userJidsSortedByUsername = Lists.newArrayList(userJids);
        userJidsSortedByUsername.sort((u1, u2) -> {
            String usernameA = profilesMap.containsKey(u1) ? profilesMap.get(u1).getUsername() : u1;
            String usernameB = profilesMap.containsKey(u2) ? profilesMap.get(u2).getUsername() : u2;
            return usernameA.compareTo(usernameB);
        });

        List<String> problemJidsSortedByAlias = problemStore.getProblemJids(contestJid);
        Set<String> problemJids = ImmutableSet.copyOf(problemJidsSortedByAlias);
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        ContestLogConfig config = new ContestLogConfig.Builder()
                .userJids(userJids)
                .problemJids(problemJids)
                .build();

        return new ContestLogsResponse.Builder()
                .data(logs)
                .config(config)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }
}
