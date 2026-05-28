package tlx.course;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import judgels.course.CourseStore;
import judgels.curriculum.CurriculumStore;
import judgels.role.TrainingAdminRoleChecker;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;
import judgels.stats.StatsStore;
import tlx.api.course.Course;
import tlx.api.course.CourseCreateData;
import tlx.api.course.CourseProgress;
import tlx.api.course.CourseUpdateData;
import tlx.api.course.CoursesResponse;
import tlx.api.curriculum.Curriculum;

@Path("/api/v2/admin/courses")
public class CourseAdminResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected TrainingAdminRoleChecker roleChecker;
    @Inject protected CourseStore courseStore;
    @Inject protected CurriculumStore curriculumStore;
    @Inject protected StatsStore statsStore;

    @Inject public CourseAdminResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public CoursesResponse getCourses(@HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);

        List<Course> courses = courseStore.getCourses();
        Optional<Curriculum> curriculum = curriculumStore.getCurriculum();

        var courseJids = Lists.transform(courses, Course::getJid);
        Map<String, CourseProgress> courseProgressMap = statsStore.getCourseProgressesMap(actorJid, courseJids);
        return new CoursesResponse.Builder()
                .data(courses)
                .curriculum(curriculum)
                .courseProgressesMap(courseProgressMap)
                .build();
    }

    @GET
    @Path("/slug/{courseSlug}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Course getCourseBySlug(
            @HeaderParam(AUTHORIZATION) Optional<AuthHeader> authHeader,
            @PathParam("courseSlug") String courseSlug) {

        actorChecker.check(authHeader);

        return checkFound(courseStore.getCourseBySlug(courseSlug));
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Course createCourse(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            CourseCreateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return courseStore.createCourse(data);
    }

    @POST
    @Path("/{courseJid}")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Course updateCourse(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("courseJid") String courseJid,
            CourseUpdateData data) {

        String actorJid = actorChecker.check(authHeader);
        checkFound(courseStore.getCourseByJid(courseJid));
        checkAllowed(roleChecker.isAdmin(actorJid));

        return courseStore.updateCourse(courseJid, data);
    }
}
