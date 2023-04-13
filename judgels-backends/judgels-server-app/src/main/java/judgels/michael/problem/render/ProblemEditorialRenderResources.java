package judgels.michael.problem.render;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.BaseProblemResource;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.ServiceUtils;

public abstract class ProblemEditorialRenderResources extends BaseProblemResource {
    @GET
    @UnitOfWork(readOnly = true)
    public Response renderEditorialMediaFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("mediaFilename") String mediaFilename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String mediaUrl = problemStore.getEditorialMediaFileURL(actor.getUserJid(), problem.getJid(), mediaFilename);
        return ServiceUtils.buildImageResponse(mediaUrl, Optional.empty());
    }

    // page path: /problems/{problemId}/editorials/edit
    // media file path: render/{mediaFilename}
    @Path("/problems/{problemId}/editorials/render/{mediaFilename}")
    public static class InEditProblemEditorial extends ProblemEditorialRenderResources {
        @Inject public InEditProblemEditorial() {}
    }

    // page path: /problems/{problemId}/editorials
    // media file path: render/{mediaFilename}
    @Path("/problems/{problemId}/render/{mediaFilename}")
    public static class InViewProblemEditorial extends ProblemEditorialRenderResources {
        @Inject public InViewProblemEditorial() {}
    }
}
