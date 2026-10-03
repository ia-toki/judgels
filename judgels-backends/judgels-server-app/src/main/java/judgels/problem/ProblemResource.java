package judgels.problem;

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
import judgels.api.problem.Problem;
import judgels.api.problem.ProblemsResponse;
import judgels.core.api.actor.AuthHeader;
import judgels.persistence.api.Page;
import judgels.profile.ProfileStore;
import judgels.session.ActorChecker;

@Path("/api/v4/problems")
public class ProblemResource {
    private static final int PAGE_SIZE = 20;

    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProfileStore profileStore;

    @Inject public ProblemResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemsResponse getProblems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @QueryParam("term") @DefaultValue("") String termFilter,
            @QueryParam("tags") Set<String> tagsFilter,
            @QueryParam("page") @DefaultValue("1") int pageNumber) {

        String actorJid = actorChecker.check(authHeader);

        Optional<String> userJid = roleChecker.isAdmin(actorJid) ? Optional.empty() : Optional.of(actorJid);
        Page<Problem> problems = problemStore.getProblems(userJid, termFilter, tagsFilter, pageNumber, PAGE_SIZE);

        var authorJids = Lists.transform(problems.getPage(), Problem::getAuthorJid);

        return new ProblemsResponse.Builder()
                .data(problems)
                .profilesMap(profileStore.getProfiles(authorJids))
                .build();
    }
}
