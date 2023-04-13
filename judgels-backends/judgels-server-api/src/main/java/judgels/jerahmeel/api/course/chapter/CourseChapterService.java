package judgels.jerahmeel.api.course.chapter;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/courses/{courseJid}/chapters")
public interface CourseChapterService {
    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    void setChapters(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("courseJid") String courseJid,
            List<CourseChapter> data);

    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    CourseChaptersResponse getChapters(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid);

    @GET
    @Path("/{chapterAlias}")
    @Produces(APPLICATION_JSON)
    CourseChapterResponse getChapter(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid,
            @PathParam("chapterAlias") String chapterAlias);

    @POST
    @Path("/user-progresses")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    CourseChapterUserProgressesResponse getChapterUserProgresses(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseJid") String courseJid,
            CourseChapterUserProgressesData data);
}
