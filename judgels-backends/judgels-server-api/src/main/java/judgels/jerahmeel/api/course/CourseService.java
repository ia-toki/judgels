package judgels.jerahmeel.api.course;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/courses")
public interface CourseService {
    @GET
    @Path("/")
    @Produces(APPLICATION_JSON)
    CoursesResponse getCourses(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader);

    @GET
    @Path("/slug/{courseSlug}")
    @Produces(APPLICATION_JSON)
    Course getCourseBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseSlug") String courseSlug);

    @POST
    @Path("/")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Course createCourse(@HeaderParam(AUTHORIZATION) AuthHeader authHeader, CourseCreateData data);

    @POST
    @Path("/{courseJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    Course updateCourse(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("courseJid") String courseJid,
            CourseUpdateData data);

}
