package judgels.uriel.contest.log;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.log.ContestLog;
import judgels.uriel.api.contest.log.ContestLogService;
import judgels.uriel.api.contest.log.ContestLogsResponse;
import judgels.uriel.contest.ContestRoleChecker;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.problem.ContestProblemStore;

public class ContestLogResource implements ContestLogService {
    private final ActorChecker actorChecker;
    private final ContestRoleChecker contestRoleChecker;
    private final ContestStore contestStore;
    private final ContestLogStore logStore;
    private final ContestProblemStore problemStore;
    private final ProfileService profileService;

    @Inject
    public ContestLogResource(
            ActorChecker actorChecker,
            ContestRoleChecker contestRoleChecker,
            ContestStore contestStore,
            ContestLogStore logStore,
            ContestProblemStore problemStore,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.contestRoleChecker = contestRoleChecker;
        this.contestStore = contestStore;
        this.logStore = logStore;
        this.problemStore = problemStore;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestLogsResponse getLogs(
            AuthHeader authHeader,
            String contestJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));

        checkAllowed(contestRoleChecker.canManage(actorJid, contest));

        Page<ContestLog> logs = logStore.getLogs(contestJid, userJid, problemJid, page);

        Set<String> userJids = logs.getPage().stream().map(ContestLog::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids, contest.getBeginTime());

        Set<String> problemJids = logs.getPage().stream()
                .filter(l -> l.getProblemJid().isPresent())
                .map(l -> l.getProblemJid().get())
                .collect(Collectors.toSet());
        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(contest.getJid(), problemJids);

        return new ContestLogsResponse.Builder()
                .data(logs)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }
}
