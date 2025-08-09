package judgels.jerahmeel.problem;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.Optional;
import java.util.Set;
import judgels.jerahmeel.api.problem.ProblemSetProblemInfo;
import judgels.jerahmeel.api.problem.ProblemsResponse;
import judgels.jerahmeel.difficulty.ProblemDifficultyStore;
import judgels.jerahmeel.stats.StatsStore;
import judgels.persistence.api.Page;
import judgels.sandalphon.SandalphonClient;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/problems")
public class ProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected StatsStore statsStore;
    @Inject protected ProblemDifficultyStore difficultyStore;
    @Inject protected SandalphonClient sandalphonClient;

    @Inject public ProblemResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @QueryParam("tags") Set<String> tags,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        // HACK: the query is very slow. In the meantime, when the number of problems is large,
        // we return empty and force the user to filter by tags.
        if (tags.isEmpty() && problemStore.getTotalProblems() > 1000) {
            return new ProblemsResponse.Builder()
                    .data(new Page.Builder<ProblemSetProblemInfo>().totalCount(0).build())
                    .build();
        }

        Set<String> allowedProblemJids = null;
        if (!tags.isEmpty()) {
            allowedProblemJids = sandalphonClient.getProblemJidsByTags(tags);
        }

        Page<ProblemSetProblemInfo> problems = problemStore.getProblems(allowedProblemJids, pageNumber, PAGE_SIZE);
        var problemJids = Lists.transform(problems.getPage(), ProblemSetProblemInfo::getProblemJid);

        return new ProblemsResponse.Builder()
                .data(problems)
                .problemsMap(sandalphonClient.getProblems(problemJids))
                .problemMetadatasMap(sandalphonClient.getProblemMetadatas(problemJids))
                .problemDifficultiesMap(difficultyStore.getProblemDifficultiesMap(problemJids))
                .problemProgressesMap(statsStore.getProblemProgressesMap(actorJid, problemJids))
                .build();
    }
}
