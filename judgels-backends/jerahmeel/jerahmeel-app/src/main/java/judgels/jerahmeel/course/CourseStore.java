package judgels.jerahmeel.course;

import static judgels.jerahmeel.JerahmeelCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import judgels.jerahmeel.api.course.Course;
import judgels.jerahmeel.api.course.CourseCreateData;
import judgels.jerahmeel.persistence.CourseDao;
import judgels.jerahmeel.persistence.CourseModel;
import judgels.jerahmeel.persistence.CourseModel_;
import judgels.persistence.FilterOptions;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class CourseStore {
    private final CourseDao courseDao;

    private final LoadingCache<String, Course> courseByJidCache;
    private final LoadingCache<String, Course> courseBySlugCache;

    @Inject
    public CourseStore(CourseDao courseDao) {
        this.courseDao = courseDao;

        this.courseByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getCourseByJidUncached);
        this.courseBySlugCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getCourseBySlugUncached);
    }

    public Optional<Course> getCourseByJid(String courseJid) {
        return Optional.ofNullable(courseByJidCache.get(courseJid));
    }

    private Course getCourseByJidUncached(String courseJid) {
        return courseDao.selectByJid(courseJid).map(CourseStore::fromModel).orElse(null);
    }

    public Optional<Course> getCourseBySlug(String courseSlug) {
        return Optional.ofNullable(courseBySlugCache.get(courseSlug));
    }

    private Course getCourseBySlugUncached(String courseSlug) {
        return courseDao.selectBySlug(courseSlug).map(CourseStore::fromModel).orElse(null);
    }

    public List<Course> getCourses() {
        return Lists.transform(courseDao.selectAll(new FilterOptions.Builder<CourseModel>()
                .addCustomPredicates((cb, cq, root) -> cb.isNotNull(root.get(CourseModel_.slug)))
                .build(), new SelectionOptions.Builder()
                .orderBy("slug")
                .orderDir(OrderDir.ASC)
                .build()), CourseStore::fromModel);
    }

    public Course createCourse(CourseCreateData data) {
        CourseModel model = new CourseModel();
        model.slug = data.getSlug();
        model.name = data.getSlug();
        model.description = "";
        return fromModel(courseDao.insert(model));
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
