package judgels.jerahmeel.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import judgels.jerahmeel.api.problem.ProblemSetProblemInfo;
import judgels.jerahmeel.api.problem.ProblemsResponse;
import judgels.jerahmeel.difficulty.ProblemDifficultyStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.sandalphon.problem.ProblemClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/problems")
public class ProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected StatsStore statsStore;
    @Inject protected ProblemDifficultyStore difficultyStore;
    @Inject protected ProblemClient problemClient;

    @Inject public ProblemResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("tags") Set<String> tags,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        Set<String> allowedProblemJids = null;
        if (!tags.isEmpty()) {
            allowedProblemJids = problemClient.getProblemJidsByTags(tags);
        }

        Page<ProblemSetProblemInfo> problems = problemStore.getProblems(allowedProblemJids, pageNumber, PAGE_SIZE);
        Set<String> problemJids = problems.getPage().stream()
                .map(ProblemSetProblemInfo::getProblemJid)
                .collect(Collectors.toSet());

        return new ProblemsResponse.Builder()
                .data(problems)
                .problemsMap(problemClient.getProblems(problemJids))
                .problemMetadatasMap(problemClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .build();
    }
}
