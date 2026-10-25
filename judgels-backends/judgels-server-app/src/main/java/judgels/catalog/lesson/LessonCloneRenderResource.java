package judgels.catalog.lesson;

import static judgels.core.JudgelsRequestChecks.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import judgels.catalog.CatalogFiles;
import judgels.catalog.lesson.statement.LessonStatementStore;
import judgels.core.JudgelsResponseBuilders;

@Path("/api/v4/lessons/{lessonJid}/clones/{userJid}")
public class LessonCloneRenderResource {
    private final LessonStore lessonStore;
    private final LessonStatementStore statementStore;

    @Inject
    public LessonCloneRenderResource(LessonStore lessonStore, LessonStatementStore statementStore) {
        this.lessonStore = lessonStore;
        this.statementStore = statementStore;
    }

    @GET
    @Path("/render/{mediaFilename}")
    @UnitOfWork(readOnly = true)
    public Response renderStatementMediaFile(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("userJid") String userJid,
            @PathParam("mediaFilename") String mediaFilename) {

        checkFound(lessonStore.getLessonByJid(lessonJid));
        CatalogFiles.checkFilename(userJid);
        CatalogFiles.checkFilename(mediaFilename);
        String mediaUrl = statementStore.getStatementMediaFileURL(userJid, lessonJid, mediaFilename);

        return JudgelsResponseBuilders.buildMediaResponse(mediaUrl, ifModifiedSince);
    }
}
