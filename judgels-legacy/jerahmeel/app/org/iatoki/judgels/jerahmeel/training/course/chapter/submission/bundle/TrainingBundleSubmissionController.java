package org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.DeprecatedControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.dependency.ChapterDependencyService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemStatus;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.course.Course;
import org.iatoki.judgels.jerahmeel.course.CourseNotFoundException;
import org.iatoki.judgels.jerahmeel.course.CourseService;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapter;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.course.chapter.CourseChapterService;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourse;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseNotFoundException;
import org.iatoki.judgels.jerahmeel.curriculum.course.CurriculumCourseService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.bundle.BundleSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.jerahmeel.training.TrainingControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.chapter.TrainingChapterControllerUtils;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.training.course.chapter.submission.bundle.html.listSubmissionsWithActionsView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmission;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BundleSubmissionService;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.bundleSubmissionView;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Singleton
public final class TrainingBundleSubmissionController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String BUNDLE_ANSWER = "bundle answer";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final FileSystemProvider bundleSubmissionLocalFileSystemProvider;
    private final FileSystemProvider bundleSubmissionRemoteFileSystemProvider;
    private final BundleSubmissionService bundleSubmissionService;
    private final CourseService courseService;
    private final CourseChapterService courseChapterService;
    private final CurriculumCourseService curriculumCourseService;
    private final CurriculumService curriculumService;
    private final ChapterDependencyService chapterDependencyService;
    private final ChapterService chapterService;
    private final ChapterProblemService chapterProblemService;

    @Inject
    public TrainingBundleSubmissionController(@BundleSubmissionLocalFileSystemProvider FileSystemProvider bundleSubmissionLocalFileSystemProvider, @BundleSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider bundleSubmissionRemoteFileSystemProvider, BundleSubmissionService bundleSubmissionService, CourseService courseService, CourseChapterService courseChapterService, CurriculumCourseService curriculumCourseService, CurriculumService curriculumService, ChapterDependencyService chapterDependencyService, ChapterService chapterService, ChapterProblemService chapterProblemService) {
        this.bundleSubmissionLocalFileSystemProvider = bundleSubmissionLocalFileSystemProvider;
        this.bundleSubmissionRemoteFileSystemProvider = bundleSubmissionRemoteFileSystemProvider;
        this.bundleSubmissionService = bundleSubmissionService;
        this.courseService = courseService;
        this.courseChapterService = courseChapterService;
        this.curriculumCourseService = curriculumCourseService;
        this.curriculumService = curriculumService;
        this.chapterDependencyService = chapterDependencyService;
        this.chapterService = chapterService;
        this.chapterProblemService = chapterProblemService;
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

        DynamicForm dForm = DynamicForm.form().bindFromRequest();

        BundleAnswer bundleAnswer = bundleSubmissionService.createBundleAnswerFromNewSubmission(dForm, DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        String submissionJid = bundleSubmissionService.submit(chapterProblem.getProblemJid(), courseChapter.getChapterJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        bundleSubmissionService.storeSubmissionFiles(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, submissionJid, bundleAnswer);

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(CHAPTER, courseChapter.getChapterJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid())), SUBMISSION, submissionJid, BUNDLE_ANSWER));

        return redirect(routes.TrainingBundleSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
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

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getBundleProblemJidToAliasMapByChapterJid(chapter.getJid());

        LazyHtml content = new LazyHtml(listOwnSubmissionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfBundleSubmissions, problemJidToAliasMap, pageIndex, orderBy, orderDir, actualProblemJid));
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        appendSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.bundle"), routes.TrainingBundleSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Bundle Submissions");

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

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionService.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, chapter.getJid());
        Map<String, String> problemJidToAliasMap = chapterProblemService.getBundleProblemJidToAliasMapByChapterJid(chapter.getJid());

        LazyHtml content;
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            content = new LazyHtml(listSubmissionsWithActionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfBundleSubmissions, problemJidToAliasMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        } else {
            content = new LazyHtml(listSubmissionsView.render(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), pageOfBundleSubmissions, problemJidToAliasMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        }
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        appendSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.bundle"), routes.TrainingBundleSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Bundle Submissions");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long curriculumId, long curriculumCourseId, long courseChapterId, long bundleSubmissionId) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, BundleSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);

        if (!JerahmeelControllerUtils.getInstance().isAdmin() && !bundleSubmission.getAuthorJid().equals(IdentityUtils.getUserJid())) {
            return redirect(routes.TrainingBundleSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()));
        }

        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(chapter.getJid(), bundleSubmission.getProblemJid());
        String chapterProblemAlias = chapterProblem.getAlias();
        String chapterProblemName = JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid());

        LazyHtml content = new LazyHtml(bundleSubmissionView.render(bundleSubmission, new Gson().fromJson(bundleSubmission.getLatestDetails(), new TypeToken<Map<String, BundleDetailResult>>() { }.getType()), bundleAnswer, JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getAuthorJid()), chapterProblemAlias, chapterProblemName, chapter.getName()));
        TrainingChapterControllerUtils.appendSubmissionSubtabLayout(content, curriculum, curriculumCourse, course, courseChapter);
        TrainingChapterControllerUtils.appendTabLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, curriculum, curriculumCourse, course, courseChapter, chapter,
                new InternalLink(Messages.get("training.submissions.bundle"), routes.TrainingBundleSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())),
                new InternalLink(bundleSubmission.getId() + "", routes.TrainingBundleSubmissionController.viewSubmission(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId(), bundleSubmission.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Bundle Submissions - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(long curriculumId, long curriculumCourseId, long courseChapterId, long bundleSubmissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, BundleSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());

        BundleSubmission bundleSubmission = bundleSubmissionService.findBundleSubmissionById(bundleSubmissionId);
        BundleAnswer bundleAnswer;
        try {
            bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));

        return redirect(routes.TrainingBundleSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long curriculumId, long curriculumCourseId, long courseChapterId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws CurriculumNotFoundException, CurriculumCourseNotFoundException, CourseNotFoundException, CourseChapterNotFoundException, ChapterNotFoundException, BundleSubmissionNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumCourse curriculumCourse = curriculumCourseService.findCurriculumCourseByCurriculumCourseId(curriculumCourseId);
        CourseChapter courseChapter = courseChapterService.findCourseChapterById(courseChapterId);

        if (!curriculum.getJid().equals(curriculumCourse.getCurriculumJid()) || !curriculumCourse.getCourseJid().equals(courseChapter.getCourseJid())) {
            return notFound();
        }

        Course course = courseService.findCourseByJid(curriculumCourse.getCourseJid());
        Chapter chapter = chapterService.findChapterByJid(courseChapter.getChapterJid());
        ListTableSelectionForm listTableSelectionData = Form.form(ListTableSelectionForm.class).bindFromRequest().get();
        List<BundleSubmission> bundleSubmissions;

        if (listTableSelectionData.selectAll) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, chapter.getJid());
        } else if (listTableSelectionData.selectJids != null) {
            bundleSubmissions = bundleSubmissionService.getBundleSubmissionsByJids(listTableSelectionData.selectJids);
        } else {
            return redirect(routes.TrainingBundleSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (BundleSubmission bundleSubmission : bundleSubmissions) {
            BundleAnswer bundleAnswer;
            try {
                bundleAnswer = bundleSubmissionService.createBundleAnswerFromPastSubmission(bundleSubmissionLocalFileSystemProvider, bundleSubmissionRemoteFileSystemProvider, bundleSubmission.getJid());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            bundleSubmissionService.regrade(bundleSubmission.getJid(), bundleAnswer, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, bundleSubmission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(bundleSubmission.getProblemJid()), SUBMISSION, bundleSubmission.getJid(), bundleSubmission.getId() + ""));
        }

        return redirect(routes.TrainingBundleSubmissionController.listSubmissions(curriculumId, curriculumCourseId, courseChapterId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    private void appendSubtabLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter) {
        if (!JerahmeelUtils.isGuest()) {
            content.appendLayout(c -> subtabLayout.render(ImmutableList.of(
                            new InternalLink(Messages.get("training.submissions.bundle.own"), routes.TrainingBundleSubmissionController.viewOwnSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())),
                            new InternalLink(Messages.get("training.submissions.bundle.all"), routes.TrainingBundleSubmissionController.viewSubmissions(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId()))
                    ), c)
            );
        }
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Curriculum curriculum, CurriculumCourse curriculumCourse, Course course, CourseChapter courseChapter, Chapter chapter, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = TrainingControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(curriculum.getName(), org.iatoki.judgels.jerahmeel.training.course.routes.TrainingCourseController.viewCourses(curriculum.getId())));
        breadcrumbsBuilder.add(new InternalLink(course.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.routes.TrainingChapterController.viewChapters(curriculum.getId(), curriculumCourse.getId())));
        breadcrumbsBuilder.add(new InternalLink(chapter.getName(), org.iatoki.judgels.jerahmeel.training.course.chapter.lesson.routes.TrainingLessonController.viewLessons(curriculum.getId(), curriculumCourse.getId(), courseChapter.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
