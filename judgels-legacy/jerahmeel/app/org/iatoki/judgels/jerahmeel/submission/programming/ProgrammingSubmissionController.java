package org.iatoki.judgels.jerahmeel.submission.programming;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.gabriel.GradingLanguageRegistry;
import org.iatoki.judgels.gabriel.SubmissionSource;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.jid.JerahmeelJidUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetNotFoundException;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblem;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblem;
import org.iatoki.judgels.jerahmeel.DeprecatedControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemService;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.chapter.problem.ChapterProblemService;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.submission.SubmissionControllerUtils;
import org.iatoki.judgels.jerahmeel.submission.programming.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.submission.programming.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.submission.programming.html.listSubmissionsWithActionsView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionUtils;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ProgrammingSubmissionController extends AbstractJudgelsController {

    private static final String SUBMISSION = "submission";
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";
    private static final String PROBLEM_SET = "problem set";
    private static final long PAGE_SIZE = 20;

    private final ProblemSetProblemService problemSetProblemService;
    private final ProblemSetService problemSetService;
    private final FileSystemProvider programmingSubmissionLocalFileSystemProvider;
    private final FileSystemProvider programmingSubmissionRemoteFileSystemProvider;
    private final ProgrammingSubmissionService programmingSubmissionService;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;

    @Inject
    public ProgrammingSubmissionController(ProblemSetProblemService problemSetProblemService, ProblemSetService problemSetService, @ProgrammingSubmissionLocalFileSystemProvider FileSystemProvider programmingSubmissionLocalFileSystemProvider, @ProgrammingSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider programmingSubmissionRemoteFileSystemProvider, ProgrammingSubmissionService programmingSubmissionService, ChapterProblemService chapterProblemService, ChapterService chapterService) {
        this.problemSetProblemService = problemSetProblemService;
        this.problemSetService = problemSetService;
        this.programmingSubmissionLocalFileSystemProvider = programmingSubmissionLocalFileSystemProvider;
        this.programmingSubmissionRemoteFileSystemProvider = programmingSubmissionRemoteFileSystemProvider;
        this.programmingSubmissionService = programmingSubmissionService;
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmissions() {
        return listOwnSubmissions(0, "id", "desc");
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result listOwnSubmissions(long pageIndex, String orderBy, String orderDir) {
        Page<ProgrammingSubmission> pageOfProgrammingSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), null, null);

        List<String> problemJids = pageOfProgrammingSubmissions.getData().stream().map(s -> s.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");
        Map<String, String> jidToNameMap = SubmissionControllerUtils.getJidToNameMap(chapterService, problemSetService, pageOfProgrammingSubmissions.getData().stream().map(s -> s.getContainerJid()).collect(Collectors.toList()));
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getGradingLanguages();

        LazyHtml content = new LazyHtml(listOwnSubmissionsView.render(pageOfProgrammingSubmissions, jidToNameMap, problemTitlesMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir));
        SubmissionControllerUtils.appendOwnSubtabLayout(content);
        SubmissionControllerUtils.appendTabLayout(content);
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("submission.own"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToOwnSubmissions()),
                new InternalLink(Messages.get("submission.programming"), routes.ProgrammingSubmissionController.viewOwnSubmissions())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Submissions - Programming");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmission(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException {
        ProgrammingSubmission programmingSubmission = programmingSubmissionService.findProgrammingSubmissionById(programmingSubmissionId);

        if (!(JerahmeelControllerUtils.getInstance().isAdmin() || programmingSubmission.getAuthorJid().equals(IdentityUtils.getUserJid()))) {
            return notFound();
        }

        LazyHtml content = getViewSubmissionContent(programmingSubmission);

        SubmissionControllerUtils.appendTabLayout(content);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("submission.own"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToOwnSubmissions()),
                new InternalLink(Messages.get("submission.programming"), routes.ProgrammingSubmissionController.viewOwnSubmissions()),
                new InternalLink(programmingSubmission.getId() + "", routes.ProgrammingSubmissionController.viewSubmission(programmingSubmission.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Submissions - Programming - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewSubmissions() {
        return listSubmissions(0, "id", "desc");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listSubmissions(long pageIndex, String orderBy, String orderDir) {
        Page<ProgrammingSubmission> pageOfProgrammingSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, null, null);

        List<String> problemJids = pageOfProgrammingSubmissions.getData().stream().map(s -> s.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), "en-US");
        Map<String, String> jidToNameMap = SubmissionControllerUtils.getJidToNameMap(chapterService, problemSetService, pageOfProgrammingSubmissions.getData().stream().map(s -> s.getContainerJid()).collect(Collectors.toList()));
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getGradingLanguages();

        LazyHtml content;
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            content = new LazyHtml(listSubmissionsWithActionsView.render(pageOfProgrammingSubmissions, jidToNameMap, problemTitlesMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir));
        } else {
            content = new LazyHtml(listSubmissionsView.render(pageOfProgrammingSubmissions, jidToNameMap, problemTitlesMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir));
        }
        SubmissionControllerUtils.appendAllSubtabLayout(content);
        if (!JerahmeelUtils.isGuest()) {
            SubmissionControllerUtils.appendTabLayout(content);
        }
        content.appendLayout(c -> heading3Layout.render(Messages.get("submission.submissions"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("submission.all"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToAllSubmissions()),
                new InternalLink(Messages.get("submission.programming"), routes.ProgrammingSubmissionController.viewSubmissions())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Submissions - Programming");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long programmingSubmissionId) throws ProgrammingSubmissionNotFoundException {
        ProgrammingSubmission programmingSubmission = programmingSubmissionService.findProgrammingSubmissionById(programmingSubmissionId);

        if (!(JerahmeelControllerUtils.getInstance().isAdmin() || programmingSubmission.getAuthorJid().equals(IdentityUtils.getUserJid()))) {
            return notFound();
        }

        LazyHtml content = getViewSubmissionContent(programmingSubmission);

        SubmissionControllerUtils.appendTabLayout(content);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("submission.all"), org.iatoki.judgels.jerahmeel.submission.routes.SubmissionController.jumpToAllSubmissions()),
                new InternalLink(Messages.get("submission.programming"), routes.ProgrammingSubmissionController.viewSubmissions()),
                new InternalLink(programmingSubmission.getId() + "", routes.ProgrammingSubmissionController.viewSubmission(programmingSubmission.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Submissions - Programming - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(String containerJid, long submissionId, long pageIndex, String orderBy, String orderDir) throws ProblemSetNotFoundException, ProgrammingSubmissionNotFoundException {
        String containerName = "";
        String logKey = "";
        if (containerJid.startsWith(JerahmeelJidUtils.PROBLEM_SET_JID_PREFIX)) {
            if (!problemSetService.problemSetExistsByJid(containerJid)) {
                return notFound();
            }

            ProblemSet problemSet = problemSetService.findProblemSetByJid(containerJid);
            containerName = problemSet.getName();
            logKey = PROBLEM_SET;
        } else {
            if (!chapterService.chapterExistsByJid(containerJid)) {
                return notFound();
            }

            Chapter chapter = chapterService.findChapterByJid(containerJid);
            containerName = chapter.getName();
            logKey = CHAPTER;
        }

        ProgrammingSubmission submission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
        programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(logKey, containerJid, containerName, PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));

        return redirect(routes.ProgrammingSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long pageIndex, String orderBy, String orderDir) throws ProgrammingSubmissionNotFoundException {
        ListTableSelectionForm data = Form.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<ProgrammingSubmission> submissions;

        if (data.selectJids != null) {
            submissions = programmingSubmissionService.getProgrammingSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.ProgrammingSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
        }

        for (ProgrammingSubmission submission : submissions) {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
            programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        }

        return redirect(routes.ProgrammingSubmissionController.listSubmissions(pageIndex, orderBy, orderDir));
    }

    private LazyHtml getViewSubmissionContent(ProgrammingSubmission programmingSubmission) {
        String containerJid;
        String containerName;
        String problemAlias;
        String problemName;
        if (programmingSubmission.getContainerJid().startsWith("JIDPRSE")) {
            ProblemSet problemSet = problemSetService.findProblemSetByJid(programmingSubmission.getContainerJid());
            containerJid = problemSet.getJid();
            containerName = problemSet.getName();

            ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(containerJid, programmingSubmission.getProblemJid());
            problemAlias = problemSetProblem.getAlias();
            problemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        } else {
            Chapter chapter = chapterService.findChapterByJid(programmingSubmission.getContainerJid());
            containerJid = chapter.getJid();
            containerName = chapter.getName();

            ChapterProblem chapterProblem = chapterProblemService.findChapterProblemByChapterJidAndProblemJid(containerJid, programmingSubmission.getProblemJid());
            problemAlias = chapterProblem.getAlias();
            problemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        }

        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, programmingSubmission.getJid());
        String authorName = JidCacheServiceImpl.getInstance().getDisplayName(programmingSubmission.getAuthorJid());
        String gradingLanguageName = GradingLanguageRegistry.getInstance().getLanguage(programmingSubmission.getGradingLanguage()).getName();

        return new LazyHtml(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(programmingSubmission.getGradingEngine()).renderViewSubmission(programmingSubmission, submissionSource, authorName, problemAlias, problemName, gradingLanguageName, containerName));
    }

    private void appendBreadcrumbsLayout(LazyHtml content, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = SubmissionControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
