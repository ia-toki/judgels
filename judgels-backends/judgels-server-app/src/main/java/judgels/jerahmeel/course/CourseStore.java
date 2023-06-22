package judgels.jerahmeel.course;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.api.course.CourseErrors;
import judgels.jerahmeel.api.course.CourseUpdateData;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CourseModel_;
import judgels.persistence.api.OrderDir;

public class CourseStore {
    private final CourseDao courseDao;

    @Inject
    public CourseStore(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public Optional<Course> getCourseByJid(String courseJid) {
        return courseDao.selectByJid(courseJid).map(CourseStore::fromModel);
    }

    public Optional<Course> getCourseBySlug(String courseSlug) {
        return courseDao.selectBySlug(courseSlug).map(CourseStore::fromModel);
    }

    public List<Course> getCourses() {
        return Lists.transform(courseDao
                .select()
                .where((cb, cq, root) -> cb.isNotNull(root.get(CourseModel_.slug)))
                .orderBy(CourseModel_.SLUG, OrderDir.ASC)
                .all(), CourseStore::fromModel);
    }

    public Course createCourse(CourseCreateData data) {
        if (courseDao.selectBySlug(data.getSlug()).isPresent()) {
            throw CourseErrors.slugAlreadyExists(data.getSlug());
        }

        CourseModel model = new CourseModel();
        model.slug = data.getSlug();
        model.name = data.getName();
        model.description = data.getDescription().orElse("");
        return fromModel(courseDao.insert(model));
    }

    public Optional<Course> updateCourse(String courseJid, CourseUpdateData data) {
        return courseDao.selectByJid(courseJid).map(model -> {
            if (data.getSlug().isPresent()) {
                String newSlug = data.getSlug().get();
                if (model.slug == null || !model.slug.equals(newSlug)) {
                    if (courseDao.selectBySlug(newSlug).isPresent()) {
                        throw CourseErrors.slugAlreadyExists(newSlug);
                    }
                }
            }

            data.getSlug().ifPresent(slug -> model.slug = slug);
            data.getName().ifPresent(name -> model.name = name);
            data.getDescription().ifPresent(description -> model.description = description);
            return fromModel(courseDao.update(model));
        });
    }

    private static Course fromModel(CourseModel model) {
        return new Course.Builder()
                .id(model.id)
                .jid(model.jid)
                .slug(model.slug)
                .name(model.name)
                .description(model.description)
                .build();
    }
}
