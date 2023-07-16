package judgels.uriel.contest.editorial;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import judgels.jophiel.JophielClient;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.SandalphonClient;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.uriel.api.contest.Contest;
import judgels.uriel.api.contest.editorial.ContestEditorialResponse;
import judgels.uriel.api.contest.module.EditorialModuleConfig;
import judgels.uriel.api.contest.problem.ContestProblem;
import judgels.uriel.contest.ContestStore;
import judgels.uriel.contest.log.ContestLogger;
import judgels.uriel.contest.module.ContestModuleStore;
import judgels.uriel.contest.problem.ContestProblemStore;

@Path("/api/v2/contests/{contestJid}/editorial")
public class ContestEditorialResource {
    @Inject protected ContestLogger contestLogger;
    @Inject protected ContestEditorialRoleChecker roleChecker;
    @Inject protected ContestStore contestStore;
    @Inject protected ContestModuleStore contestModuleStore;
    @Inject protected ContestProblemStore problemStore;
    @Inject protected JophielClient jophielClient;
    @Inject protected SandalphonClient sandalphonClient;

    @Inject public ContestEditorialResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ContestEditorialResponse getEditorial(
            @Context UriInfo uriInfo,
            @PathParam("contestJid") String contestJid,
            @QueryParam("language") Optional<String> language) {

        Contest contest = checkFound(contestStore.getContestByJid(contestJid));
        checkAllowed(roleChecker.canView(contest));

        EditorialModuleConfig config = checkFound(contestModuleStore.getEditorialModuleConfig(contestJid));

        List<ContestProblem> problems = problemStore.getProblems(contestJid);

        var problemJids = Lists.transform(problems, ContestProblem::getProblemJid);
        Map<String, ProblemInfo> problemsMap = sandalphonClient.getProblems(problemJids);
        Map<String, ProblemMetadata> problemMetadatasMap = sandalphonClient.getProblemMetadatas(problemJids);
        Map<String, ProblemEditorialInfo> problemEditorialsMap = sandalphonClient.getProblemEditorials(problemJids, uriInfo.getBaseUri(), language);

        Map<String, Profile> profilesMap = new HashMap<>();
        profilesMap.putAll(jophielClient.parseProfiles(config.getPreface().orElse("")));
        profilesMap.putAll(jophielClient.getProfiles(problemMetadatasMap.values()
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
