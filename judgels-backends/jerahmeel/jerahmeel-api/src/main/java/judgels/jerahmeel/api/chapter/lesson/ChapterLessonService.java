package judgels.jerahmeel.api.chapter.lesson;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/chapters/{chapterJid}/lessons")
public interface ChapterLessonService {
    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void setLessons(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("chapterJid") String chapterJid,
            List<ChapterLessonData> data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    ChapterLessonsResponse getLessons(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid);

    @GET
    @Path("/{lessonAlias}/statement")
    @Produces(APPLICATION_JSON)
    ChapterLessonStatement getLessonStatement(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("chapterJid") String chapterJid,
            @PathParam("lessonAlias") String lessonAlias);
}
