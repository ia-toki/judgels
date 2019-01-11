package org.iatoki.judgels.jerahmeel.training.course;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.training.TrainingCurriculumControllerUtils;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseWithProgress;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.training.TrainingControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.html.listCurriculumCoursesView;
import org.iatoki.judgels.jerahmeel.training.course.html.listCurriculumCoursesWithProgressView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class TrainingCourseController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final CurriculumCourseService curriculumCourseService;
    private final CurriculumService curriculumService;
    private final CourseService courseService;

    @Inject
    public TrainingCourseController(CurriculumCourseService curriculumCourseService, CurriculumService curriculumService, CourseService courseService) {
        this.curriculumCourseService = curriculumCourseService;
        this.curriculumService = curriculumService;
        this.courseService = courseService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result viewCourses(long curriculumId) throws CurriculumNotFoundException {
        return listCourses(curriculumId, 0, "alias", "asc", "");
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result listCourses(long curriculumId, long page, String orderBy, String orderDir, String filterString) throws CurriculumNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);

        LazyHtml content;
        if (!JerahmeelUtils.isGuest()) {
            Page<CurriculumCourseWithProgress> pageOfCurriculumCoursesWithProgress = curriculumCourseService.getPageOfCurriculumCoursesWithProgress(IdentityUtils.getUserJid(), curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCoursesWithProgress.getData().stream().map(e -> e.getCurriculumCourse().getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            content = new LazyHtml(listCurriculumCoursesWithProgressView.render(curriculum.getId(), pageOfCurriculumCoursesWithProgress, coursesMap, orderBy, orderDir, filterString));
        } else {
            Page<CurriculumCourse> pageOfCurriculumCourses = curriculumCourseService.getPageOfCurriculumCourses(curriculum.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> courseJids = pageOfCurriculumCourses.getData().stream().map(e -> e.getCourseJid()).collect(Collectors.toList());
            Map<String, Course> coursesMap = courseService.getCoursesMapByJids(courseJids);

            content = new LazyHtml(listCurriculumCoursesView.render(curriculum.getId(), pageOfCurriculumCourses, coursesMap, orderBy, orderDir, filterString));
        }

        TrainingCurriculumControllerUtils.appendTabLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculums");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = TrainingControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(curriculum.getName(), routes.TrainingCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
