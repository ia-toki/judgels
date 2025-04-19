package judgels.michael.lesson.render;

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
import judgels.michael.lesson.BaseLessonResource;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.service.ServiceUtils;

public abstract class LessonStatementRenderResources extends BaseLessonResource {
    @GET
    @UnitOfWork(readOnly = true)
    public Response renderStatementMediaFile(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @PathParam("mediaFilename") String mediaFilename) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.canView(actor, lesson));

        String mediaUrl = statementStore.getStatementMediaFileURL(actor.getUserJid(), lesson.getJid(), mediaFilename);
        return ServiceUtils.buildMediaResponse(mediaUrl, Optional.empty());
    }

    // page path: /lessons/{lessonId}/statements/edit
    // media file path: render/{mediaFilename}
    @Path("/lessons/{lessonId}/statements/render/{mediaFilename}")
    public static class InEditLessonStatement extends LessonStatementRenderResources {
        @Inject
        public InEditLessonStatement() {}
    }

    // page path: /lessons/{lessonId}/statements
    // media file path: render/{mediaFilename}
    @Path("/lessons/{lessonId}/render/{mediaFilename}")
    public static class InViewLessonStatement extends LessonStatementRenderResources {
        @Inject public InViewLessonStatement() {}
    }
}
