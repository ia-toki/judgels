package judgels.michael.problem.render;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.BaseProblemResource;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.ServiceUtils;

public abstract class ProblemStatementRenderResources extends BaseProblemResource {
    @GET
    @UnitOfWork(readOnly = true)
    public Response renderStatementMediaFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("mediaFilename") String mediaFilename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String mediaUrl = statementStore.getStatementMediaFileURL(actor.getUserJid(), problem.getJid(), mediaFilename);
        return ServiceUtils.buildMediaResponse(mediaUrl, Optional.empty());
    }

    // page path: /problems/{problemId}/statements/edit
    // media file path: render/{mediaFilename}
    @Path("/problems/{problemId}/statements/render/{mediaFilename}")
    public static class InEditProblemStatement extends ProblemStatementRenderResources {
        @Inject public InEditProblemStatement() {}
    }

    // page path: /problems/programming/{problemId}/statements
    // media file path: render/{mediaFilename}
    @Path("/problems/programming/{problemId}/render/{mediaFilename}")
    public static class InViewProgrammingProblemStatement extends ProblemStatementRenderResources {
        @Inject public InViewProgrammingProblemStatement() {}
    }

    // page path: /problems/bundle/{problemId}/statements
    // media file path: render/{mediaFilename}
    @Path("/problems/bundle/{problemId}/render/{mediaFilename}")
    public static class InViewBundleProblemStatement extends ProblemStatementRenderResources {
        @Inject public InViewBundleProblemStatement() {}
    }
}
