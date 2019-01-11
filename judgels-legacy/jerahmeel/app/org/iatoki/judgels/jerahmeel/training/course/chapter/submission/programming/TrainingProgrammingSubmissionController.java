package org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.gabriel.GradingLanguageRegistry;
import org.iatoki.judgels.gabriel.SubmissionSource;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemStatus;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.training.TrainingControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.chapter.TrainingChapterControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.programming.html.listSubmissionsWithActionsView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionUtils;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
public final class TrainingProgrammingSubmissionController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String PROGRAMMING_FILES = "programming_files";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final CourseService courseService;
    private final CourseChapterService courseChapterService;
    private final CurriculumCourseService curriculumCourseService;
    private final CurriculumService curriculumService;
    private final ChapterDependencyService chapterDependencyService;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;
    private final FileSystemProvider programmingSubmissionLocalFileSystemProvider;
    private final FileSystemProvider programmingSubmissionRemoteFileSystemProvider;
    private final ProgrammingSubmissionService programmingSubmissionService;

    @Inject
    public TrainingProgrammingSubmissionController(CourseService courseService, CourseChapterService courseChapterService, CurriculumCourseService curriculumCourseService, CurriculumService curriculumService, ChapterDependencyService chapterDependencyService, ChapterProblemService chapterProblemService, ChapterService chapterService, @ProgrammingSubmissionLocalFileSystemProvider FileSystemProvider programmingSubmissionLocalFileSystemProvider, @ProgrammingSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider programmingSubmissionRemoteFileSystemProvider, ProgrammingSubmissionService programmingSubmissionService) {
        this.courseService = courseService;
        this.courseChapterService = courseChapterService;
        this.curriculumCourseService = curriculumCourseService;
        this.curriculumService = curriculumService;
        this.chapterDependencyService = chapterDependencyService;
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
        this.programmingSubmissionLocalFileSystemProvider = programmingSubmissionLocalFileSystemProvider;
        this.programmingSubmissionRemoteFileSystemProvider = programmingSubmissionRemoteFileSystemProvider;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional
    public Result postSubmitProblem(long curriculumId, long curriculumCourseId, long courseChapterId, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseChapterNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid()) || !chapterDependencyService.isDependenciesFulfilled(IdentityUtils.getUserJid(), courseChapter.getChapterJid())) {
            return notFound();
        }

        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(courseChapter.getChapterJid(), problemJid);

        if (chapterProblem.getStatus() != ChapterProblemStatus.VISIBLE) {
            return notFound();
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();

        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];
        String gradingEngine = body.asFormUrlEncoded().get("engine")[0];

        String submissionJid;
        try {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromNewSubmission(body);
            submissionJid = programmingSubmissionService.submit(problemJid, courseChapter.getChapterJid(), gradingEngine, gradingLanguage, null, submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            ProgrammingSubmissionUtils.storeSubmissionFiles(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submissionJid, submissionSource);

        } catch (ProgrammingSubmissionException e) {
            flash("submissionError", e.getMessage());

            return redirect(org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.viewProblem(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), chapterProblem.getId()));
        }

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(CHAPTER, courseChapter.getChapterJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid())), SUBMISSION, submissionJid, PROGRAMMING_FILES));

        return redirect(routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        return listOwnSubmissions(curriculumId, curriculumCourseId, courseChapterId, 0, "id", "desc", null);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result listOwnSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId, long pageIndex, String orderBy, String orderDir, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<ProgrammingSubmission> pageOfSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getProgrammingProblemJidToAliasMapByChapterJid(chapter.getJid());
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getGradingLanguages();

        LazyHtml content = new LazyHtml(listOwnSubmissionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfSubmissions, problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualProblemJid));
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        appendSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.programming"), routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Programming Submissions");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        return listSubmissions(curriculumId, curriculumCourseId, courseChapterId, 0, "id", "desc", null, null);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        String actualUserJid = "(none)".equals(userJid) ? null : userJid;
        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<ProgrammingSubmission> pageOfSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getProgrammingProblemJidToAliasMapByChapterJid(chapter.getJid());
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getGradingLanguages();

        LazyHtml content;
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            content = new LazyHtml(listSubmissionsWithActionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfSubmissions, problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        } else {
            content = new LazyHtml(listSubmissionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfSubmissions, problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        }
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        appendSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.programming"), routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Programming Submissions");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long curriculumId, long curriculumCourseId, long courseChapterId, long submissionId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        ProgrammingSubmission programmingSubmission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);

        if (!JerahmeelControllerUtils.getInstance().isAdmin() && !programmingSubmission.getAuthorJid().equals(IdentityUtils.getUserJid())) {
            return redirect(routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        }

        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, programmingSubmission.getJid());
        String authorName = JidCacheServiceImpl.getInstance().getDisplayName(programmingSubmission.getAuthorJid());
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), programmingSubmission.getProblemJid());
        String chapterProblemAlias = chapterProblem.getAlias();
        String chapterProblemName = JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid());
        String gradingLanguageName = GradingLanguageRegistry.getInstance().getLanguage(programmingSubmission.getGradingLanguage()).getName();

        LazyHtml content = new LazyHtml(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(programmingSubmission.getGradingEngine()).renderViewSubmission(programmingSubmission, submissionSource, authorName, chapterProblemAlias, chapterProblemName, gradingLanguageName, chapter.getName()));
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.programming"), routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())),
                new InternalLink(programmingSubmission.getId() + "", routes.TrainingProgrammingSubmissionController.viewSubmission(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), programmingSubmission.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Programming Submissions - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(long curriculumId, long curriculumCourseId, long courseChapterId, long programmingSubmissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        ProgrammingSubmission submission = programmingSubmissionService.findProgrammingSubmissionById(programmingSubmissionId);
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
        programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));

        return redirect(routes.TrainingProgrammingSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, ProgrammingSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        ListTableSelectionForm data = Form.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<ProgrammingSubmission> submissions;

        if (data.selectAll) {
            submissions = programmingSubmissionService.getProgrammingSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, chapter.getJid());
        } else if (data.selectJids != null) {
            submissions = programmingSubmissionService.getProgrammingSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.TrainingProgrammingSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (ProgrammingSubmission submission : submissions) {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
            programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));
        }

        return redirect(routes.TrainingProgrammingSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    private void appendSubtabLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter) {
        if (!JerahmeelUtils.isGuest()) {
            content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                            new InternalLink(Messages.get("training.submissions.programming.own"), routes.TrainingProgrammingSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())),
                            new InternalLink(Messages.get("training.submissions.programming.all"), routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
                    ), c)
            );
        }
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = TrainingControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(curriculum.getName(), org.iatoki.judgels.jerahmeel.training.course.routes.TrainingCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(new InternalLink(course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())));
        breadcrumbsBuilder.add(new InternalLink(chapter.getName(), routes.TrainingProgrammingSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
