package judgels.jerahmeel.course;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.CourseUpdateData;
import judgels.jerahmeel.api.course.CoursesResponse;
import judgels.jerahmeel.api.curriculum.Curriculum;
import judgels.jerahmeel.curriculum.CurriculumStore;
import judgels.jerahmeel.role.RoleChecker;
import judgels.jerahmeel.stats.StatsStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class CourseResource implements CourseService {
    private final ActorChecker actorChecker;
    private final RoleChecker roleChecker;
    private final CourseStore courseStore;
    private final CurriculumStore curriculumStore;
    private final StatsStore statsStore;

    @Inject
    public CourseResource(
            ActorChecker actorChecker,
            RoleChecker roleChecker,
            CourseStore courseStore,
            CurriculumStore curriculumStore,
            StatsStore statsStore) {

        this.actorChecker = actorChecker;
        this.roleChecker = roleChecker;
        this.courseStore = courseStore;
        this.curriculumStore = curriculumStore;
        this.statsStore = statsStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CoursesResponse getCourses(Optional<AuthHeader> authHeader) {
        String actorJid = actorChecker.check(authHeader);

        List<Course> courses = courseStore.getCourses();
        Optional<Curriculum> curriculum = curriculumStore.getCurriculum();
        Set<String> courseJids = courses.stream().map(Course::getJid).collect(Collectors.toSet());
        Map<String, CourseProgress> courseProgressMap = statsStore.getCourseProgressesMap(actorJid, courseJids);
        return new CoursesResponse.Builder()
                .data(courses)
                .curriculum(curriculum)
                .courseProgressesMap(courseProgressMap)
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Course getCourseBySlug(Optional<AuthHeader> authHeader, String courseSlug) {
        actorChecker.check(authHeader);

        return checkFound(courseStore.getCourseBySlug(courseSlug));
    }

    @Override
    @UnitOfWork
    public Course createCourse(AuthHeader authHeader, CourseCreateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return courseStore.createCourse(data);
    }

    @Override
    @UnitOfWork
    public Course updateCourse(AuthHeader authHeader, String courseJid, CourseUpdateData data) {
        String actorJid = actorChecker.check(authHeader);
        checkAllowed(roleChecker.isAdmin(actorJid));

        return checkFound(courseStore.updateCourse(courseJid, data));
    }
}
