package judgels.jerahmeel.course;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.persistence.api.SelectionOptions;

public class CourseStore {
    private final CourseDao courseDao;

    @Inject
    public CourseStore(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> getCourses() {
        return Lists.transform(courseDao.selectAll(SelectionOptions.DEFAULT_ALL), CourseStore::fromModel);
    }

    private static Course fromModel(CourseModel model) {
        return new Course.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(Optional.ofNullable(model.description))
                .build();
    }
}
