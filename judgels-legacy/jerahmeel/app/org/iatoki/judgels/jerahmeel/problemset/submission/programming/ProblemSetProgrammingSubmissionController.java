package org.iatoki.judgels.jerahmeel.problemset.submission.programming;

import com.google.common.collect.ImmutableList;
import judgels.gabriel.api.SubmissionSource;
import judgels.gabriel.languages.GradingLanguageRegistry;
import org.iatoki.judgels.FileSystemProvider;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.DeprecatedControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.activity.JerahmeelActivityKeys;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetNotFoundException;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblem;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemService;
import org.iatoki.judgels.jerahmeel.problemset.problem.ProblemSetProblemStatus;
import org.iatoki.judgels.jerahmeel.problemset.submission.AbstractProblemSetSubmissionController;
import org.iatoki.judgels.jerahmeel.problemset.submission.programming.html.listOwnSubmissionsView;
import org.iatoki.judgels.jerahmeel.problemset.submission.programming.html.listSubmissionsView;
import org.iatoki.judgels.jerahmeel.problemset.submission.programming.html.listSubmissionsWithActionsView;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionLocalFileSystemProvider;
import org.iatoki.judgels.jerahmeel.submission.programming.ProgrammingSubmissionRemoteFileSystemProvider;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmission;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionNotFoundException;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionService;
import org.iatoki.judgels.sandalphon.problem.programming.submission.ProgrammingSubmissionUtils;
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

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ProblemSetProgrammingSubmissionController extends AbstractProblemSetSubmissionController {

    private static final long PAGE_SIZE = 20;
    private static final String SUBMISSION = "submission";
    private static final String PROGRAMMING_FILES = "programming_files";
    private static final String PROBLEM = "problem";
    private static final String PROBLEM_SET = "problem_set";

    private final FileSystemProvider programmingSubmissionLocalFileSystemProvider;
    private final FileSystemProvider programmingSubmissionRemoteFileSystemProvider;
    private final ProblemSetProblemService problemSetProblemService;
    private final ProblemSetService problemSetService;
    private final ProgrammingSubmissionService programmingSubmissionService;

    @Inject
    public ProblemSetProgrammingSubmissionController(@ProgrammingSubmissionLocalFileSystemProvider FileSystemProvider programmingSubmissionLocalFileSystemProvider, @ProgrammingSubmissionRemoteFileSystemProvider @Nullable FileSystemProvider programmingSubmissionRemoteFileSystemProvider, ProblemSetProblemService problemSetProblemService, ProblemSetService problemSetService, ProgrammingSubmissionService programmingSubmissionService) {
        this.programmingSubmissionLocalFileSystemProvider = programmingSubmissionLocalFileSystemProvider;
        this.programmingSubmissionRemoteFileSystemProvider = programmingSubmissionRemoteFileSystemProvider;
        this.problemSetProblemService = problemSetProblemService;
        this.problemSetService = problemSetService;
        this.programmingSubmissionService = programmingSubmissionService;
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional
    public Result postSubmitProblem(long problemSetId, String problemJid) throws ProblemSetNotFoundException {
        if (!isAdmin()) {
            return notFound();
        }

        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(problemSet.getJid(), problemJid);

        if (problemSetProblem.getStatus() != ProblemSetProblemStatus.VISIBLE) {
            return notFound();
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();

        String gradingLanguage = body.asFormUrlEncoded().get("language")[0];
        String gradingEngine = body.asFormUrlEncoded().get("engine")[0];

        String submissionJid;
        try {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromNewSubmission(body);
            submissionJid = programmingSubmissionService.submit(problemJid, problemSet.getJid(), gradingEngine, gradingLanguage, null, submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            ProgrammingSubmissionUtils.storeSubmissionFiles(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submissionJid, submissionSource);

        } catch (ProgrammingSubmissionException e) {
            flash("submissionError", e.getMessage());

            return redirect(org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewProblemSetProblem(problemSet.getId(), problemSetProblem.getId()));
        }

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.SUBMIT.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, problemSetProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid())), SUBMISSION, submissionJid, PROGRAMMING_FILES));

        return redirect(routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSet.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewOwnSubmissions(long problemSetId) throws ProblemSetNotFoundException {
        return listOwnSubmissions(problemSetId, 0, "id", "desc", null);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result listOwnSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String problemJid) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<ProgrammingSubmission> pageOfSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, IdentityUtils.getUserJid(), actualProblemJid, problemSet.getJid());
        Map<String, String> problemJidToAliasMap = problemSetProblemService.getProgrammingProblemJidToAliasMapByProblemSetJid(problemSet.getJid());
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getNamesMap();

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listOwnSubmissionsView.render(problemSet.getId(), pageOfSubmissions, problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualProblemJid));
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.setPageTitle("Problem Sets - Programming Submissions");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewSubmissions(long problemSetId) throws ProblemSetNotFoundException {
        return listSubmissions(problemSetId, 0, "id", "desc", null, null);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        String actualUserJid = "(none)".equals(userJid) ? null : userJid;
        String actualProblemJid = "(none)".equals(problemJid) ? null : problemJid;

        Page<ProgrammingSubmission> pageOfSubmissions = programmingSubmissionService.getPageOfProgrammingSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, actualUserJid, actualProblemJid, problemSet.getJid());
        Map<String, String> problemJidToAliasMap = problemSetProblemService.getProgrammingProblemJidToAliasMapByProblemSetJid(problemSet.getJid());
        Map<String, String> gradingLanguageToNameMap = GradingLanguageRegistry.getInstance().getNamesMap();

        HtmlTemplate template = getBaseHtmlTemplate();
        if (JerahmeelControllerUtils.getInstance().isAdmin()) {
            template.setContent(listSubmissionsWithActionsView.render(problemSet.getId(), pageOfSubmissions, ImmutableList.of(), problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        } else {
            template.setContent(listSubmissionsView.render(problemSet.getId(), pageOfSubmissions, ImmutableList.of(), problemJidToAliasMap, gradingLanguageToNameMap, pageIndex, orderBy, orderDir, actualUserJid, actualProblemJid));
        }
        template.setSecondaryTitle(Messages.get("submission.submissions"));
        template.markBreadcrumbLocation(Messages.get("archive.problemSet.submissions.programming.all"), routes.ProblemSetProgrammingSubmissionController.viewSubmissions(problemSet.getId()));
        template.setPageTitle("Problem Sets - Programming Submissions");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Transactional(readOnly = true)
    public Result viewSubmission(long problemSetId, long submissionId) throws ProblemSetNotFoundException, ProgrammingSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ProgrammingSubmission submission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);

        if (!JerahmeelControllerUtils.getInstance().isAdmin() && !submission.getAuthorJid().equals(IdentityUtils.getUserJid())) {
            return redirect(routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSetId));
        }

        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
        String authorName = JidCacheServiceImpl.getInstance().getDisplayName(submission.getAuthorJid());
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemByProblemSetJidAndProblemJid(problemSet.getJid(), submission.getProblemJid());
        String problemSetProblemAlias = problemSetProblem.getAlias();
        String problemSetProblemName = SandalphonResourceDisplayNameUtils.parseTitleByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()), DeprecatedControllerUtils.getHardcodedDefaultLanguage());
        String gradingLanguageName = GradingLanguageRegistry.getInstance().get(submission.getGradingLanguage()).getName();

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(submission.getGradingEngine()).renderViewSubmission(submission, submissionSource, authorName, problemSetProblemAlias, problemSetProblemName, gradingLanguageName, problemSet.getName()));
        template.markBreadcrumbLocation(submission.getId() + "", routes.ProblemSetProgrammingSubmissionController.viewSubmission(problemSet.getId(), submission.getId()));
        template.setPageTitle("Problem Sets - Programming Submissions - View");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmission(long problemSetId, long submissionId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException, ProgrammingSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ProgrammingSubmission submission = programmingSubmissionService.findProgrammingSubmissionById(submissionId);
        SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
        programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));

        return redirect(routes.ProblemSetProgrammingSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result regradeSubmissions(long problemSetId, long pageIndex, String orderBy, String orderDir, String userJid, String problemJid) throws ProblemSetNotFoundException, ProgrammingSubmissionNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ListTableSelectionForm data = Form.form(ListTableSelectionForm.class).bindFromRequest().get();

        List<ProgrammingSubmission> submissions;

        if (data.selectAll) {
            submissions = programmingSubmissionService.getProgrammingSubmissionsByFilters(orderBy, orderDir, userJid, problemJid, problemSet.getJid());
        } else if (data.selectJids != null) {
            submissions = programmingSubmissionService.getProgrammingSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.ProblemSetProgrammingSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
        }

        for (ProgrammingSubmission submission : submissions) {
            SubmissionSource submissionSource = ProgrammingSubmissionUtils.createSubmissionSourceFromPastSubmission(programmingSubmissionLocalFileSystemProvider, programmingSubmissionRemoteFileSystemProvider, submission.getJid());
            programmingSubmissionService.regrade(submission.getJid(), submissionSource, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

            JerahmeelControllerUtils.getInstance().addActivityLog(JerahmeelActivityKeys.REGRADE.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, submission.getProblemJid(), JidCacheServiceImpl.getInstance().getDisplayName(submission.getProblemJid()), SUBMISSION, submission.getJid(), submission.getId() + ""));
        }

        return redirect(routes.ProblemSetProgrammingSubmissionController.listSubmissions(problemSetId, pageIndex, orderBy, orderDir, userJid, problemJid));
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemSet problemSet) {
        appendTabs(template, problemSet);

        if (!JerahmeelUtils.isGuest()) {
            template.addTertiaryTab(Messages.get("archive.problemSet.submissions.programming.own"), routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSet.getId()));
            template.addTertiaryTab(Messages.get("archive.problemSet.submissions.programming.all"), routes.ProblemSetProgrammingSubmissionController.viewSubmissions(problemSet.getId()));
        }
        template.markBreadcrumbLocation(Messages.get("archive.problemSet.submissions.programming"), routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSet.getId()));

        return super.renderTemplate(template, problemSet);
    }
}
