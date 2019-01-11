package org.iatoki.judgels.jerahmeel.controllers.api.internal;

import org.iatoki.judgels.AutoComplete;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public final class InternalCourseAPIController extends AbstractJudgelsAPIController {

    private final CourseService courseService;

    @Inject
    public InternalCourseAPIController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Authenticated(LoggedIn.class)
    @Transactional
    public Result autocompleteCourse(String term) {
        List<Course> courses = courseService.getCoursesByTerm(term);
        List<AutoComplete> autocompletedCourses = courses.stream()
                .map(c -> new AutoComplete("" + c.getId(), c.getJid(), c.getName()))
                .collect(Collectors.toList());
        return okAsJson(autocompletedCourses);
    }
}
