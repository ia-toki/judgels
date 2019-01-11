package org.iatoki.judgels.jerahmeel.course;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.iatoki.judgels.play.Page;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class CourseServiceImpl implements CourseService {

    private final CourseDao courseDao;

    @Inject
    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public boolean courseExistsByJid(String courseJid) {
        return courseDao.existsByJid(courseJid);
    }

    @Override
    public Map<String, Course> getCoursesMapByJids(List<String> courseJids) {
        List<CourseModel> courseModels = courseDao.findSortedByFiltersIn("id", "asc", "", ImmutableMap.of(CourseModel_.jid, courseJids), 0, -1);
        return courseModels.stream().collect(Collectors.toMap(m -> m.jid, m -> CourseServiceUtils.createCourseFromModel(m)));
    }

    @Override
    public List<Course> getCoursesByTerm(String term) {
        List<CourseModel> courses = courseDao.findSortedByFilters("id", "asc", term, 0, -1);
        ImmutableList.Builder<Course> courseBuilder = ImmutableList.builder();

        for (CourseModel course : courses) {
            courseBuilder.add(CourseServiceUtils.createCourseFromModel(course));
        }

        return courseBuilder.build();
    }

    @Override
    public Page<Course> getPageOfCourses(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString) {
        long totalPages = courseDao.countByFilters(filterString, ImmutableMap.of(), ImmutableMap.of());
        List<CourseModel> courseModels = courseDao.findSortedByFilters(orderBy, orderDir, filterString, pageIndex * pageSize, pageSize);

        List<Course> courses = Lists.transform(courseModels, m -> CourseServiceUtils.createCourseFromModel(m));

        return new Page<>(courses, totalPages, pageIndex, pageSize);
    }

    @Override
    public Course findCourseById(long courseId) throws CourseNotFoundException {
        CourseModel courseModel = courseDao.findById(courseId);
        if (courseModel != null) {
            return CourseServiceUtils.createCourseFromModel(courseModel);
        } else {
            throw new CourseNotFoundException("Course not found.");
        }
    }

    @Override
    public Course findCourseByJid(String courseJid) {
        CourseModel courseModel = courseDao.findByJid(courseJid);

        return CourseServiceUtils.createCourseFromModel(courseModel);
    }

    @Override
    public Course createCourse(String name, String description, String userJid, String userIpAddress) {
        CourseModel courseModel = new CourseModel();
        courseModel.name = name;
        courseModel.description = description;

        courseDao.persist(courseModel, userJid, userIpAddress);

        return CourseServiceUtils.createCourseFromModel(courseModel);
    }

    @Override
    public void updateCourse(String courseJid, String name, String description, String userJid, String userIpAddress) {
        CourseModel courseModel = courseDao.findByJid(courseJid);
        courseModel.name = name;
        courseModel.description = description;

        courseDao.edit(courseModel, userJid, userIpAddress);
    }
}
