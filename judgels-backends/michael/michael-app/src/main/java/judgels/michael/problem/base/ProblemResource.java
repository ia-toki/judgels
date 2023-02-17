package judgels.michael.problem.base;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import judgels.michael.MichaelConfiguration;
import judgels.michael.actor.Actor;
import judgels.michael.actor.ActorChecker;
import judgels.michael.template.HtmlTemplate;

@Path("/problems")
public class ProblemResource extends BaseProblemResource {
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
    public View listProblems(@Context HttpServletRequest req) {
        Actor actor = actorChecker.check(req);

        HtmlTemplate template = newTemplate(actor);
        template.setTitle("Problems");
        return new ListProblemsView(template);
    }
}
