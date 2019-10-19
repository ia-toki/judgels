package judgels.jerahmeel.course;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CourseModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class CourseStore {
    private final CourseDao courseDao;

    @Inject
    public CourseStore(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public List<Course> getCourses() {
        return Lists.transform(courseDao.selectAll(new FilterOptions.Builder<CourseModel>()
                .addCustomPredicates((cb, cq, root) -> cb.isNotNull(root.get(CourseModel_.slug)))
                .build(), new SelectionOptions.Builder()
                .orderBy("slug")
                .orderDir(OrderDir.ASC)
                .build()), CourseStore::fromModel);
    }

    private static Course fromModel(CourseModel model) {
        return new Course.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .description(Optional.ofNullable(model.description))
                .build();
    }
}
