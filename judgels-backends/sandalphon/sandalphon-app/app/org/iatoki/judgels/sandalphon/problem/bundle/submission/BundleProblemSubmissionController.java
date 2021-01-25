package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.forms.ListTableSelectionForm;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.base.submission.SubmissionFs;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleDetailResult;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.bundleSubmissionView;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.html.listSubmissionsView;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class BundleProblemSubmissionController extends AbstractProblemController {
    private static final long PAGE_SIZE = 20;

    private final ObjectMapper mapper;
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final FileSystem bundleSubmissionFs;
    private final BundleSubmissionStore bundleSubmissionStore;
    private final ProfileService profileService;

    @Inject
    public BundleProblemSubmissionController(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            @SubmissionFs FileSystem bundleSubmissionFs,
            BundleSubmissionStore bundleSubmissionStore,
            ProfileService profileService) {

        super(problemStore, problemRoleChecker);
        this.mapper = mapper;
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.bundleSubmissionFs = bundleSubmissionFs;
        this.bundleSubmissionStore = bundleSubmissionStore;
        this.profileService = profileService;
    }

    @Transactional
    public Result postSubmit(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);

        Problem problem = checkFound(problemStore.findProblemById(problemId));

        boolean isClean = !problemStore.userCloneExists(actorJid, problem.getJid());
        checkAllowed(problemRoleChecker.isAllowedToSubmit(req, problem) && isClean);

        DynamicForm dForm = formFactory.form().bindFromRequest(req);

        BundleAnswer bundleAnswer = bundleSubmissionStore.createBundleAnswerFromNewSubmission(dForm, getCurrentStatementLanguage(req));
        String submissionJid = bundleSubmissionStore.submit(problem.getJid(), null, bundleAnswer);
        bundleSubmissionStore.storeSubmissionFiles(bundleSubmissionFs, null, submissionJid, bundleAnswer);

        return redirect(routes.BundleProblemSubmissionController.viewSubmissions(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewSubmissions(Http.Request req, long problemId)  {
        return listSubmissions(req, problemId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listSubmissions(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToSubmit(req, problem));

        Page<BundleSubmission> pageOfBundleSubmissions = bundleSubmissionStore.getPageOfBundleSubmissions(pageIndex, PAGE_SIZE, orderBy, orderDir, null, problem.getJid(), null);

        Set<String> userJids = pageOfBundleSubmissions.getPage().stream().map(BundleSubmission::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listSubmissionsView.render(pageOfBundleSubmissions, problemId, profilesMap, pageIndex, orderBy, orderDir));
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.viewSubmissions(problemId));
        template.setPageTitle("Problem - Submissions");

        return renderTemplate(template, problem);
    }

    @Transactional(readOnly = true)
    public Result viewSubmission(Http.Request req, long problemId, long submissionId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToSubmit(req, problem));

        BundleSubmission bundleSubmission = checkFound(bundleSubmissionStore.findBundleSubmissionById(submissionId));
        BundleAnswer bundleAnswer = bundleSubmissionStore.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());

        Profile profile = profileService.getProfile(bundleSubmission.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(bundleSubmissionView.render(bundleSubmission, parseGradingResult(bundleSubmission), bundleAnswer, profile, null, problem.getSlug(), null));

        template.markBreadcrumbLocation("View submission", org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmission(problemId, submissionId));
        template.setPageTitle("Problem - View submission");

        return renderTemplate(template, problem);
    }

    @Transactional
    public Result regradeSubmission(Http.Request req, long problemId, long submissionId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToSubmit(req, problem));

        BundleSubmission bundleSubmission = checkFound(bundleSubmissionStore.findBundleSubmissionById(submissionId));
        BundleAnswer bundleAnswer = bundleSubmissionStore.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());
        bundleSubmissionStore.regrade(bundleSubmission.getJid(), bundleAnswer);

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    @Transactional
    public Result regradeSubmissions(Http.Request req, long problemId, long pageIndex, String orderBy, String orderDir) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToSubmit(req, problem));

        ListTableSelectionForm data = formFactory.form(ListTableSelectionForm.class).bindFromRequest(req).get();

        List<BundleSubmission> submissions;

        if (data.selectAll) {
            submissions = bundleSubmissionStore.getBundleSubmissionsByFilters(orderBy, orderDir, null, problem.getJid(), null);
        } else if (data.selectJids != null) {
            submissions = bundleSubmissionStore.getBundleSubmissionsByJids(data.selectJids);
        } else {
            return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
        }

        for (BundleSubmission bundleSubmission : submissions) {
            BundleAnswer bundleAnswer = bundleSubmissionStore.createBundleAnswerFromPastSubmission(bundleSubmissionFs, null, bundleSubmission.getJid());
            bundleSubmissionStore.regrade(bundleSubmission.getJid(), bundleAnswer);
        }

        return redirect(routes.BundleProblemSubmissionController.listSubmissions(problemId, pageIndex, orderBy, orderDir));
    }

    protected Result renderTemplate(HtmlTemplate template, Problem problem) {
        template.markBreadcrumbLocation("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToSubmissions(problem.getId()));

        return super.renderTemplate(template, problem);
    }

    protected Map<String, BundleDetailResult> parseGradingResult(BundleSubmission submission) {
        Map<String, BundleDetailResult> gradingResult;
        try {
            gradingResult = mapper.readValue(submission.getLatestDetails(), new TypeReference<Map<String, BundleDetailResult>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<Long, String> numberToJidMap = Maps.newTreeMap();
        for (Map.Entry<String, BundleDetailResult> entry : gradingResult.entrySet()) {
            numberToJidMap.put(entry.getValue().getNumber(), entry.getKey());
        }

        ImmutableMap.Builder<String, BundleDetailResult> sortedGradingResult = ImmutableMap.builder();
        for (Map.Entry<Long, String> entry : numberToJidMap.entrySet()) {
            String currentJid = numberToJidMap.get(entry.getKey());
            sortedGradingResult.put(currentJid, gradingResult.get(currentJid));
        }

        return sortedGradingResult.build();
    }
}
