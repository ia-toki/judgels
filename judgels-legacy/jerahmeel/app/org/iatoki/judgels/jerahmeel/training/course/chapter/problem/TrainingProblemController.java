package org.iatoki.judgels.jerahmeel.training.course.chapter.problem;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemStatus;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemType;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemWithProgress;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.training.TrainingControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.chapter.TrainingChapterControllerUtils;
import org.iatoki.judgels.jerahmeel.user.item.UserItemService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.training.course.chapter.problem.html.listChapterProblemsView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.problem.html.listChapterProblemsWithProgressView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.problem.html.viewProblemView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class TrainingProblemController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;

    private final CourseService courseService;
    private final CourseChapterService courseChapterService;
    private final CurriculumCourseService curriculumCourseService;
    private final CurriculumService curriculumService;
    private final SandalphonClientAPI sandalphonClientAPI;
    private final ChapterDependencyService chapterDependencyService;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;
    private final UserItemService userItemService;

    @Inject
    public TrainingProblemController(CourseService courseService, CourseChapterService courseChapterService, CurriculumCourseService curriculumCourseService, CurriculumService curriculumService, SandalphonClientAPI sandalphonClientAPI, ChapterDependencyService chapterDependencyService, ChapterProblemService chapterProblemService, ChapterService chapterService, UserItemService userItemService) {
        this.courseService = courseService;
        this.courseChapterService = courseChapterService;
        this.curriculumCourseService = curriculumCourseService;
        this.curriculumService = curriculumService;
        this.sandalphonClientAPI = sandalphonClientAPI;
        this.chapterDependencyService = chapterDependencyService;
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
        this.userItemService = userItemService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewProblems(long curriculumId, long curriculumCourseId, long courseChapterId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        return listProblems(curriculumId, curriculumCourseId, courseChapterId, 0, "alias", "asc", "");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listProblems(long curriculumId, long curriculumCourseId, long courseChapterId, long page, String orderBy, String orderDir, String filterString) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        LazyHtml content;
        if (!JerahmeelUtils.isGuest()) {
            Page<ChapterProblemWithProgress> pageOfChapterProblemsWithProgress = chapterProblemService.getPageOfChapterProblemsWithProgress(IdentityUtils.getUserJid(), courseChapter.getChapterJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> problemJids = pageOfChapterProblemsWithProgress.getData().stream().map(cp -> cp.getChapterProblem().getProblemJid()).collect(Collectors.toList());
            Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), StatementControllerUtils.getCurrentStatementLanguage());

            content = new LazyHtml(listChapterProblemsWithProgressView.render(curriculum, curriculumCourse, courseChapter, pageOfChapterProblemsWithProgress, orderBy, orderDir, filterString, problemTitlesMap));
        } else {
            Page<ChapterProblem> pageOfChapterProblems = chapterProblemService.getPageOfChapterProblems(courseChapter.getChapterJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> problemJids = pageOfChapterProblems.getData().stream().map(cp -> cp.getProblemJid()).collect(Collectors.toList());
            Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), StatementControllerUtils.getCurrentStatementLanguage());

            content = new LazyHtml(listChapterProblemsView.render(curriculum, curriculumCourse, courseChapter, pageOfChapterProblems, orderBy, orderDir, filterString, problemTitlesMap));
        }

        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Training");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewProblem(long curriculumId, long curriculumCourseId, long courseChapterId, long chapterProblemId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, ChapterProblemNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if ((chapterProblem.getStatus() != ChapterProblemStatus.VISIBLE) || !curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid()) || !chapterProblem.getChapterJid().equals(courseChapter.getChapterJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        String reasonNotAllowedToSubmit = null;
        if (JerahmeelUtils.isGuest()) {
            reasonNotAllowedToSubmit = Messages.get("training.chapter.problem.mustLogin");
        } else if (!chapterDependencyService.isDependenciesFulfilled(IdentityUtils.getUserJid(), chapter.getJid())) {
            reasonNotAllowedToSubmit = Messages.get("training.chapter.isLocked");
        }

        String requestUrl;
        String requestBody;

        if (ChapterProblemType.BUNDLE.equals(chapterProblem.getType())) {
            SandalphonBundleProblemStatementRenderRequestParam param = new SandalphonBundleProblemStatementRenderRequestParam();

            param.setProblemSecret(chapterProblem.getProblemSecret());
            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(routes.TrainingProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.routes.TrainingBundleSubmissionController.postSubmitProblem(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), chapterProblem.getProblemJid()).absoluteURL(request(), request().secure()));
            param.setReasonNotAllowedToSubmit(reasonNotAllowedToSubmit);

            requestUrl = sandalphonClientAPI.getBundleProblemStatementRenderAPIEndpoint(chapterProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructBundleProblemStatementRenderAPIRequestBody(chapterProblem.getProblemJid(), param);
        } else if (ChapterProblemType.PROGRAMMING.equals(chapterProblem.getType())) {
            SandalphonProgrammingProblemStatementRenderRequestParam param = new SandalphonProgrammingProblemStatementRenderRequestParam();

            param.setProblemSecret(chapterProblem.getProblemSecret());
            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(routes.TrainingProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.routes.TrainingProgrammingSubmissionController.postSubmitProblem(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), chapterProblem.getProblemJid()).absoluteURL(request(), request().secure()));
            param.setReasonNotAllowedToSubmit(reasonNotAllowedToSubmit);
            param.setAllowedGradingLanguages("");

            requestUrl = sandalphonClientAPI.getProgrammingProblemStatementRenderAPIEndpoint(chapterProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructProgrammingProblemStatementRenderAPIRequestBody(chapterProblem.getProblemJid(), param);
        } else {
            throw new IllegalStateException();
        }

        session("problemJid", chapterProblem.getProblemJid());

        LazyHtml content = new LazyHtml(viewProblemView.render(requestUrl, requestBody, chapterProblem.getId()));
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(chapterProblem.getAlias(), routes.TrainingProblemController.viewProblem(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), chapterProblem.getId()))
        );

        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Training");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = GuestView.class)
    public Result switchLanguage() {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        StatementControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result renderProblemMedia(long curriculumId, long curriculumCourseId, long courseChapterId, long chapterProblemId, String filename) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, ChapterProblemNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if ((chapterProblem.getStatus() != ChapterProblemStatus.VISIBLE) || !curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid()) || !chapterProblem.getChapterJid().equals(courseChapter.getChapterJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        String mediaUrl = sandalphonClientAPI.getProblemStatementMediaRenderAPIEndpoint(chapterProblem.getProblemJid(), filename);

        return redirect(mediaUrl);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = TrainingControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(curriculum.getName(), org.iatoki.judgels.jerahmeel.training.course.routes.TrainingCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(new InternalLink(course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())));
        breadcrumbsBuilder.add(new InternalLink(chapter.getName(), routes.TrainingProblemController.viewProblems(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
