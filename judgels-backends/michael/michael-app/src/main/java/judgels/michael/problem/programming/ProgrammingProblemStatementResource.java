package judgels.michael.problem.programming;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.actor.ActorChecker;
import judgels.michael.problem.base.BaseProblemResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemStore;

@Path("/problems/programming/{problemId}/statements")
public class ProgrammingProblemStatementResource extends BaseProblemResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemStore problemStore;

    @Inject public ProgrammingProblemStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        return new ViewStatementView(template, problem);
    }
}
