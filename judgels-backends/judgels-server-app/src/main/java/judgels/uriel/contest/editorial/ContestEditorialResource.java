package judgels.uriel.contest.editorial;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.user.UserClient;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.sandalphon.problem.ProblemClient;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.editorial.ContestEditorialResponse;
import judgels.uriel.api.contest.editorial.ContestEditorialService;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;

public class ContestEditorialResource implements ContestEditorialService {
    private final ContestLogger contestLogger;
    private final ContestEditorialRoleChecker roleChecker;
    private final ContestStore contestStore;
    private final ContestModuleStore contestModuleStore;
    private final ContestProblemStore problemStore;
    private final UserClient userClient;
    private final ProblemClient problemClient;

    @Inject
    public ContestEditorialResource(
            ContestLogger contestLogger,
            ContestEditorialRoleChecker roleChecker,
            ContestStore contestStore,
            ContestModuleStore contestModuleStore,
            ContestProblemStore problemStore,
            UserClient userClient, ProblemClient problemClient) {
        this.contestLogger = contestLogger;
        this.roleChecker = roleChecker;
        this.contestStore = contestStore;
        this.contestModuleStore = contestModuleStore;
        this.problemStore = problemStore;
        this.userClient = userClient;
        this.problemClient = problemClient;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ContestEditorialResponse getEditorial(String contestJid, Optional<String> language) {
        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canView(contest));

        EditorialModuleConfig config = checkFound(contestModuleStore.getEditorialModuleConfig(contestJid));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);
        Set<String> problemJids = problems.stream().map(ContestProblem::getProblemJid).collect(Collectors.toSet());

        Map<String, ProblemInfo> problemsMap = problemClient.getProblems(problemJids);
        Map<String, ProblemMetadata> problemMetadatasMap = problemClient.getProblemMetadatas(problemJids);
        Map<String, ProblemEditorialInfo> problemEditorialsMap =
                problemClient.getProblemEditorials(problemJids, language);

        Map<String, Profile> profilesMap = Maps.newHashMap();
        profilesMap.putAll(userClient.parseProfiles(config.getPreface().orElse("")));
        profilesMap.putAll(userClient.getProfiles(problemMetadatasMap.values()
                .stream()
                .map(ProblemMetadata::getSettersMap)
                .map(Map::values)
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())));

        contestLogger.log(contestJid, "OPEN_EDITORIAL");

        return new ContestEditorialResponse.Builder()
                .preface(config.getPreface())
                .problems(problems)
                .problemsMap(problemsMap)
                .problemMetadatasMap(problemMetadatasMap)
                .problemEditorialsMap(problemEditorialsMap)
                .profilesMap(profilesMap)
                .build();
    }
}
