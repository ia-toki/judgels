package judgels.jerahmeel.api.course.chapter;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/courses/{courseJid}/chapters")
public interface CourseChapterService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    CourseChaptersResponse getChapters(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid);

    @GET
    @Path("/{chapterAlias}")
    @Produces(APPLICATION_JSON)
    Chapter getChapter(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid,
            @PathParam("chapterAlias") String chapterAlias);
}
