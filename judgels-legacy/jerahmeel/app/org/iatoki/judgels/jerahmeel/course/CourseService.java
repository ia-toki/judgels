package org.iatoki.judgels.jerahmeel.course;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.Page;

import java.util.List;
import java.util.Map;

@ImplementedBy(CourseServiceImpl.class)
public interface CourseService {

    boolean courseExistsByJid(String courseJid);

    Map<String, Course> getCoursesMapByJids(List<String> courseJids);

    List<Course> getCoursesByTerm(String term);

    Page<Course> getPageOfCourses(long pageIndex, long pageSize, String orderBy, String orderDir, String filterString);

    Course findCourseById(long courseId) throws CourseNotFoundException;

    Course findCourseByJid(String courseJid);

    Course createCourse(String name, String description, String userJid, String userIpAddress);

    void updateCourse(String courseJid, String name, String description, String userJid, String userIpAddress);
}
