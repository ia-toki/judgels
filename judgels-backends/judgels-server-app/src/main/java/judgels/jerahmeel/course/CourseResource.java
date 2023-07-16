package judgels.jerahmeel.course;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.api.course.CourseUpdateData;
import judgels.jerahmeel.api.course.CoursesResponse;
import judgels.jerahmeel.api.curriculum.Curriculum;
import judgels.jerahmeel.curriculum.CurriculumStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

@Path("/api/v2/courses")
public class CourseResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected RoleChecker roleChecker;
    @Inject protected CourseStore courseStore;
    @Inject protected CurriculumStore curriculumStore;
    @Inject protected StatsStore statsStore;

    @Inject public CourseResource() {}

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
