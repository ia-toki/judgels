package judgels.michael.problem.base;

import io.dropwizard.hibernate.UnitOfWork;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.michael.actor.ActorChecker;

@Path("/problems")
public class ProblemResource {
    private final ActorChecker actorChecker;

    @Inject
    public ProblemResource(ActorChecker actorChecker) {
        this.actorChecker = actorChecker;
    }

    @GET
    @UnitOfWork(readOnly = true)
    public Response listProblems(@Context HttpServletRequest req) {
        String actorJid = actorChecker.check(req);

        return Response.ok(actorJid).build();
    }
}
