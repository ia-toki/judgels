package org.iatoki.judgels.jerahmeel.course.chapter;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseControllerUtils;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.course.chapter.html.editCourseChapterView;
import org.iatoki.judgels.jerahmeel.course.chapter.html.listAddCourseChaptersView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class CourseChapterController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String CHAPTER = "chapter";
    private static final String COURSE = "course";

    private final CourseService courseService;
    private final CourseChapterService courseChapterService;
    private final ChapterService chapterService;

    @Inject
    public CourseChapterController(CourseService courseService, CourseChapterService courseChapterService, ChapterService chapterService) {
        this.courseService = courseService;
        this.courseChapterService = courseChapterService;
        this.chapterService = chapterService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewChapters(long courseId) throws CourseNotFoundException {
        return listAddChapters(courseId, 0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result listAddChapters(long courseId, long page, String orderBy, String orderDir, String filterString) throws CourseNotFoundException {
        Course course = courseService.findCourseById(courseId);

        Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(course.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
        Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

        Form<CourseChapterAddForm> courseChapterAddForm = Form.form(CourseChapterAddForm.class);

        return showListAddChapters(course, courseChapterAddForm, pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddChapter(long courseId, long page, String orderBy, String orderDir, String filterString) throws CourseNotFoundException {
        Course course = courseService.findCourseById(courseId);
        Form<CourseChapterAddForm> courseChapterCreateForm = Form.form(CourseChapterAddForm.class).bindFromRequest();

        if (formHasErrors(courseChapterCreateForm)) {
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(course.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);

            return showListAddChapters(course, courseChapterCreateForm, pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString);
        }

        CourseChapterAddForm courseChapterCreateData = courseChapterCreateForm.get();
        if (!chapterService.chapterExistsByJid(courseChapterCreateData.chapterJid)) {
            courseChapterCreateForm.reject(Messages.get("error.course.invalidJid"));
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(course.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);


            return showListAddChapters(course, courseChapterCreateForm, pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString);
        }

        if (courseChapterService.existsByCourseJidAndAlias(course.getJid(), courseChapterCreateData.alias)) {
            courseChapterCreateForm.reject(Messages.get("error.course.chapter.duplicateAlias"));
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(course.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);


            return showListAddChapters(course, courseChapterCreateForm, pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString);
        }

        if (courseChapterService.existsByCourseJidAndChapterJid(course.getJid(), courseChapterCreateData.chapterJid)) {
            courseChapterCreateForm.reject(Messages.get("error.course.chapterExist"));
            Page<CourseChapter> pageOfCourseChapters = courseChapterService.getPageOfCourseChapters(course.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> chapterJids = pageOfCourseChapters.getData().stream().map(e -> e.getChapterJid()).collect(Collectors.toList());
            Map<String, Chapter> chaptersMap = chapterService.getChaptersMapByJids(chapterJids);


            return showListAddChapters(course, courseChapterCreateForm, pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString);
        }

        CourseChapter courseChapter = courseChapterService.addCourseChapter(course.getJid(), courseChapterCreateData.chapterJid, courseChapterCreateData.alias, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(COURSE, course.getJid(), course.getName(), CHAPTER, courseChapter.getChapterJid(), chapter.getName()));

        return redirect(routes.CourseChapterController.viewChapters(course.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editChapter(long courseId, long courseChapterId) throws CourseNotFoundException, CourseChapterNotFoundException {
        Course course = courseService.findCourseById(courseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!courseChapter.getCourseJid().equals(course.getJid())) {
            return notFound();
        }

        CourseChapterEditForm courseChapterEditData = new CourseChapterEditForm();
        courseChapterEditData.alias = courseChapter.getAlias();
        Form<CourseChapterEditForm> courseChapterEditForm = Form.form(CourseChapterEditForm.class).fill(courseChapterEditData);

        return showEditChapter(course, courseChapter, courseChapterEditForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditChapter(long courseId, long courseChapterId) throws CourseNotFoundException, CourseChapterNotFoundException {
        Course course = courseService.findCourseById(courseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!courseChapter.getCourseJid().equals(course.getJid())) {
            return notFound();
        }

        Form<CourseChapterEditForm> courseChapterEditForm = Form.form(CourseChapterEditForm.class).bindFromRequest();
        if (formHasErrors(courseChapterEditForm)) {
            return showEditChapter(course, courseChapter, courseChapterEditForm);
        }

        CourseChapterEditForm courseChapterEditData = courseChapterEditForm.get();
        if (!courseChapterEditData.alias.equals(courseChapter.getAlias()) && courseChapterService.existsByCourseJidAndAlias(course.getJid(), courseChapterEditData.alias)) {
            courseChapterEditForm.reject(Messages.get("error.course.chapter.duplicateAlias"));
            return showEditChapter(course, courseChapter, courseChapterEditForm);
        }

        courseChapterService.updateCourseChapter(courseChapter.getId(), courseChapterEditData.alias, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(COURSE, course.getJid(), course.getName(), CHAPTER, courseChapter.getChapterJid(), chapter.getName()));

        return redirect(routes.CourseChapterController.viewChapters(course.getId()));
    }

    @Transactional
    public Result removeChapter(long courseId, long courseChapterId) throws CourseNotFoundException, CourseChapterNotFoundException {
        Course course = courseService.findCourseById(courseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        if (!course.getJid().equals(courseChapter.getCourseJid())) {
            return forbidden();
        }

        courseChapterService.removeCourseChapter(courseChapterId);

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(COURSE, course.getJid(), course.getName(), CHAPTER, courseChapter.getChapterJid(), chapter.getName()));

        return redirect(routes.CourseChapterController.viewChapters(course.getId()));
    }

    private Result showListAddChapters(Course course, Form<CourseChapterAddForm> courseChapterAddForm, Page<CourseChapter> pageOfCourseChapters, Map<String, Chapter> chaptersMap, String orderBy, String orderDir, String filterString) {
        LazyHtml content = new LazyHtml(listAddCourseChaptersView.render(course.getId(), pageOfCourseChapters, chaptersMap, orderBy, orderDir, filterString, courseChapterAddForm));
        CourseControllerUtils.appendTabLayout(content, course);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, course,
                new InternalLink(Messages.get("commons.view"), routes.CourseChapterController.viewChapters(course.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Courses");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditChapter(Course course, CourseChapter courseChapter, Form<CourseChapterEditForm> courseChapterEditForm) {
        LazyHtml content = new LazyHtml(editCourseChapterView.render(courseChapterEditForm, course.getId(), courseChapter.getId()));
        CourseControllerUtils.appendTabLayout(content, course);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, course,
                new InternalLink(Messages.get("commons.update"), routes.CourseChapterController.editChapter(course.getId(), courseChapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Courses");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Course course, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = CourseControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("course.chapters"), org.iatoki.judgels.jerahmeel.course.routes.CourseController.jumpToChapters(course.getId())));
        breadcrumbsBuilder.add(new InternalLink(Messages.get("commons.view"), routes.CourseChapterController.viewChapters(course.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
