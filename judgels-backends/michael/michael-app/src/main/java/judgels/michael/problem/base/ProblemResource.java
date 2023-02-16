package judgels.michael.problem.base;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.michael.BaseResource;
import judgels.michael.MichaelConfiguration;
import judgels.michael.actor.Actor;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;

@Path("/problems")
public class ProblemResource extends BaseResource {
    private final ActorChecker actorChecker;

    @Inject
    public ProblemResource(
            MichaelConfiguration config,
            ActorChecker actorChecker) {

        super(config);
        this.actorChecker = actorChecker;
    }

    @GET
    @UnitOfWork(readOnly = true)
    public Response listProblems(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);
        HtmlTemplate template = newTemplate(actor);

        return renderView(new ListProblemsView(template));
    }
}
