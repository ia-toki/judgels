package org.iatoki.judgels.jerahmeel.training.course.chapter;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterWithProgress;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.training.course.AbstractTrainingCourseController;
import org.iatoki.judgels.jerahmeel.training.course.chapter.html.listCourseChaptersView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.html.listCourseChaptersWithProgressView;
import org.iatoki.judgels.jerahmeel.training.course.routes;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class TrainingChapterController extends AbstractTrainingCourseController {

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

        HtmlTemplate template = getBaseHtmlTemplate();
        if (!JerahmeelUtils.isGuest()) {
            Page<CourseChapterWithProgress> pageOfCourseChaptersWithProgress = courseChapterService.getPageOfCourseChaptersWithProgress(IdentityUtils.getUserJid(), curriculumCourse.getCourseJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChaptersWithProgress.getData().stream().map(e -> e.getCourseChapter().getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

            template.setContent(listCourseChaptersWithProgressView.render(curriculum.getId(), curriculumCourse.getId(), pageOfCourseChaptersWithProgress, chaptersMap, orderBy, orderDir, filterString));
        } else {
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(curriculumCourse.getCourseJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

            template.setContent(listCourseChaptersView.render(curriculum.getId(), curriculumCourse.getId(), pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString));
        }

        if (!course.getDescription().isEmpty()) {
            template.setDescription(course.getDescription());
        }
        if (JerahmeelUtils.hasRole("admin")) {
            template.setMainTitle(Messages.get("course.course") + " #" + course.getId() + ": " + course.getName());
            template.addMainButton(Messages.get("commons.update"), org.iatoki.judgels.jerahmeel.course.routes.CourseController.editCourseGeneral(course.getId()));
        } else {
            template.setMainTitle(Messages.get("course.course") + " " + curriculumCourse.getAlias() + ": " + course.getName());
        }
        template.setMainBackButton(Messages.get("training.backTo") + " " + curriculum.getName(), routes.TrainingCourseController.viewCourses(curriculum.getId()));

        template.setPageTitle("Training");

        return renderTemplate(template, curriculum, curriculumCourse, course);
    }
}
