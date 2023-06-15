package judgels.sandalphon.lesson;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.sandalphon.lesson.statement.LessonStatementStore;
import judgels.service.ServiceUtils;

@Path("/api/v2/lessons/{lessonJid}")
public class LessonResource {
    private final LessonStore lessonStore;
    private final LessonStatementStore statementStore;

    @Inject
    public LessonResource(LessonStore lessonStore, LessonStatementStore statementStore) {
        this.lessonStore = lessonStore;
        this.statementStore = statementStore;
    }

    @GET
    @Path("/render/{mediaFilename}")
    @UnitOfWork
    public Response renderStatementImage(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("mediaFilename") String mediaFilename) {

        checkFound(lessonStore.getLessonByJid(lessonJid));
        String mediaUrl = statementStore.getStatementMediaFileURL(null, lessonJid, mediaFilename);

        return ServiceUtils.buildMediaResponse(mediaUrl, ifModifiedSince);
    }
}
