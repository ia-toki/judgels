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

public abstract class ProblemEditorialRenderResources extends BaseProblemResource {
    @GET
    @UnitOfWork(readOnly = true)
    public Response renderEditorialMediaFile(
            @Context HttpServletRequest req,
            @PathParam("problemId") int problemId,
            @PathParam("mediaFilename") String mediaFilename) {

        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.getProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        String mediaUrl = editorialStore.getEditorialMediaFileURL(actor.getUserJid(), problem.getJid(), mediaFilename);
        return ServiceUtils.buildMediaResponse(mediaUrl, Optional.empty());
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
