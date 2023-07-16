package judgels.jerahmeel.problem;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import java.util.Set;
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
