package judgels.jerahmeel.course;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseService;
import judgels.jerahmeel.api.course.CoursesResponse;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class CourseResource implements CourseService {
    private final ActorChecker actorChecker;
    private final CourseStore courseStore;

    @Inject
    public CourseResource(ActorChecker actorChecker, CourseStore courseStore) {
        this.actorChecker = actorChecker;
        this.courseStore = courseStore;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public CoursesResponse getCourses(Optional<AuthHeader> authHeader) {
        List<Course> courses = courseStore.getCourses();
        return new CoursesResponse.Builder()
                .data(courses)
                .build();
    }
}
