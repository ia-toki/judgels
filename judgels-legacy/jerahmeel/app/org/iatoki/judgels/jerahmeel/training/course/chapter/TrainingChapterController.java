package org.iatoki.judgels.jerahmeel.training.course.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterWithProgress;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.training.TrainingControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.TrainingCourseControllerUtils;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.jerahmeel.training.course.chapter.html.listCourseChaptersView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.html.listCourseChaptersWithProgressView;
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
public final class TrainingChapterController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final CurriculumService curriculumService;
    private final CurriculumCourseService curriculumCourseService;
    private final CourseService courseService;
    private final CourseChapterService courseChapterService;
    private final UserItemService userItemService;
    private final ChapterService chapterService;

    @Inject
    public TrainingChapterController(CurriculumService curriculumService, CurriculumCourseService curriculumCourseService, CourseService courseService, CourseChapterService courseChapterService, UserItemService userItemService, ChapterService chapterService) {
        this.curriculumService = curriculumService;
        this.curriculumCourseService = curriculumCourseService;
        this.courseService = courseService;
        this.courseChapterService = courseChapterService;
        this.userItemService = userItemService;
        this.chapterService = chapterService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result viewChapters(long curriculumId, long curriculumCourseId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException {
        return listChapters(curriculumId, curriculumCourseId, 0, "alias", "asc", "");
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result listChapters(long curriculumId, long curriculumCourseId, long page, String orderBy, String orderDir, String filterString) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());

        LazyHtml content;
        if (!JerahmeelUtils.isGuest()) {
            Page<CourseChapterWithProgress> pageOfCourseChaptersWithProgress = courseChapterService.getPageOfCourseChaptersWithProgress(IdentityUtils.getUserJid(), curriculumCourse.getCourseJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChaptersWithProgress.getData().stream().map(e -> e.getCourseChapter().getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

            content = new LazyHtml(listCourseChaptersWithProgressView.render(curriculum.getId(), curriculumCourse.getId(), pageOfCourseChaptersWithProgress, chaptersMap, orderBy, orderDir, filterString));
        } else {
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(curriculumCourse.getCourseJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

            content = new LazyHtml(listCourseChaptersView.render(curriculum.getId(), curriculumCourse.getId(), pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString));
        }

        TrainingCourseControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Training");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = TrainingControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(curriculum.getName(), org.iatoki.judgels.jerahmeel.training.course.routes.TrainingCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(new InternalLink(course.getName(), routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
