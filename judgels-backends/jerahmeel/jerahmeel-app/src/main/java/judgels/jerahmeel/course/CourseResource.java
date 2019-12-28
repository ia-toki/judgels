package judgels.jerahmeel.course;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.CoursesResponse;
import judgels.jerahmeel.curriculum.CurriculumStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class CourseResource implements CourseService {
    private final ActorChecker actorChecker;
    private final CourseStore courseStore;
    private final CurriculumStore curriculumStore;

    @Inject
    public CourseResource(ActorChecker actorChecker, CourseStore courseStore, CurriculumStore curriculumStore) {
        this.actorChecker = actorChecker;
        this.courseStore = courseStore;
        this.curriculumStore = curriculumStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CoursesResponse getCourses(Optional<AuthHeader> authHeader) {
        actorChecker.check(authHeader);

        return new CoursesResponse.Builder()
                .data(courseStore.getCourses())
                .curriculumDescription(curriculumStore.getCurriculumDescription())
                .build();
    }

    @Override
    @UnitOfWork(readOnly = true)
    public Course getCourseBySlug(Optional<AuthHeader> authHeader, String courseSlug) {
        actorChecker.check(authHeader);

        return checkFound(courseStore.getCourseBySlug(courseSlug));
    }
}
