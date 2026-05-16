package judgels.contest.editorial;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.api.contest.Contest;
import judgels.api.contest.editorial.ContestEditorialResponse;
import judgels.api.contest.module.EditorialModuleConfig;
import judgels.api.contest.problem.ContestProblem;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.profile.Profile;
import judgels.contest.ContestStore;
import judgels.contest.log.ContestLogger;
import judgels.contest.module.ContestModuleStore;
import judgels.contest.problem.ContestProblemStore;
import judgels.jophiel.JophielClient;
import judgels.sandalphon.SandalphonClient;

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
            @Context HttpServletRequest req,
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
        Map<String, ProblemEditorialInfo> problemEditorialsMap = sandalphonClient.getProblemEditorials(req, uriInfo, problemJids, language);

        Map<String, Profile> profilesMap = Maps.newHashMap();
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
