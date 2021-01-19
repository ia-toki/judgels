package org.iatoki.judgels.sandalphon.problem.base;

import static judgels.service.ServiceUtils.checkFound;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.html.createProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.editProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.listProblemsView;
import org.iatoki.judgels.sandalphon.problem.base.html.viewProblemView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class ProblemController extends AbstractBaseProblemController {

    private static final long PAGE_SIZE = 20;

    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public ProblemController(ProblemService problemService, ProfileService profileService) {
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listProblems(0, "updatedAt", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listProblems(long pageIndex, String sortBy, String orderBy, String filterString) {
        boolean isAdmin = SandalphonControllerUtils.getInstance().isAdmin();
        boolean isWriter = SandalphonControllerUtils.getInstance().isWriter();
        Page<Problem> pageOfProblems = problemService.getPageOfProblems(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, IdentityUtils.getUserJid(), isAdmin);

        Set<String> userJids = pageOfProblems.getPage().stream().map(Problem::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listProblemsView.render(pageOfProblems, profilesMap, sortBy, orderBy, filterString, isWriter));
        if (isWriter) {
            template.addMainButton("Create", routes.ProblemController.createProblem());
        }
        template.setMainTitle("Problems");
        template.setPageTitle("Problems");
        return renderTemplate(template);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProblem() {
        Form<ProblemCreateForm> problemCreateForm = formFactory.form(ProblemCreateForm.class);

        return showCreateProblem(problemCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProblem() {
        Form<ProblemCreateForm> problemCreateForm = formFactory.form(ProblemCreateForm.class).bindFromRequest();

        if (formHasErrors(problemCreateForm)) {
            return showCreateProblem(problemCreateForm);
        }

        if (problemService.problemExistsBySlug(problemCreateForm.get().slug)) {
            return showCreateProblem(problemCreateForm.withError("slug", "Slug already exists"));
        }

        ProblemCreateForm problemCreateData = problemCreateForm.get();
        ProblemControllerUtils.setJustCreatedProblem(problemCreateData.slug, problemCreateData.additionalNote, problemCreateData.initLanguageCode);

        if (problemCreateData.type.equals(ProblemType.PROGRAMMING.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.createProgrammingProblem());
        } else if (problemCreateData.type.equals(ProblemType.BUNDLE.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.createBundleProblem());
        }

        return internalServerError();
    }

    public Result enterProblem(long problemId) {
        return redirect(routes.ProblemController.jumpToStatement(problemId));
    }

    public Result jumpToStatement(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
    }

    public Result jumpToVersions(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.version.routes.ProblemVersionController.viewVersionLocalChanges(problemId));
    }

    public Result jumpToPartners(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problemId));
    }

    @Transactional(readOnly = true)
    public Result viewProblem(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));
        if (!ProblemControllerUtils.isAllowedToViewStatement(problemService, problem)) {
            return notFound();
        }

        Profile profile = profileService.getProfile(problem.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewProblemView.render(problem, profile));
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.addMainButton("Enter problem", routes.ProblemController.enterProblem(problem.getId()));
        template.markBreadcrumbLocation("View problem", routes.ProblemController.viewProblem(problem.getId()));
        template.setPageTitle("Problem - View");
        return renderProblemTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblem(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return redirect(routes.ProblemController.viewProblem(problem.getId()));
        }

        ProblemEditForm problemEditData = new ProblemEditForm();
        problemEditData.slug = problem.getSlug();
        problemEditData.additionalNote = problem.getAdditionalNote();

        Form<ProblemEditForm> problemEditForm = formFactory.form(ProblemEditForm.class).fill(problemEditData);

        return showEditProblem(problemEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditProblem(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return notFound();
        }

        Form<ProblemEditForm> problemEditForm = formFactory.form(ProblemEditForm.class).bindFromRequest();

        if (formHasErrors(problemEditForm)) {
            return showEditProblem(problemEditForm, problem);
        }

        if (!problem.getSlug().equals(problemEditForm.get().slug) && problemService.problemExistsBySlug(problemEditForm.get().slug)) {
            return showEditProblem(problemEditForm.withError("slug", "Slug already exists"), problem);
        }

        ProblemEditForm problemEditData = problemEditForm.get();
        problemService.updateProblem(problem.getJid(), problemEditData.slug, problemEditData.additionalNote, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.ProblemController.viewProblem(problem.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(long problemId) {
        String languageCode = formFactory.form().bindFromRequest().get("langCode");
        ProblemControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeaders().get("Referer").orElse(""));
    }

    private Result showCreateProblem(Form<ProblemCreateForm> problemCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createProblemView.render(problemCreateForm));
        template.setMainTitle("Create problem");
        template.markBreadcrumbLocation("Create problem", routes.ProblemController.createProblem());
        template.setPageTitle("Problem - Create");
        return renderTemplate(template);
    }

    private Result showEditProblem(Form<ProblemEditForm> problemEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editProblemView.render(problemEditForm, problem));
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.addMainButton("Enter problem", routes.ProblemController.enterProblem(problem.getId()));
        template.markBreadcrumbLocation("Update problem", routes.ProblemController.editProblem(problem.getId()));
        template.setPageTitle("Problem - Update");
        return renderProblemTemplate(template, problemService, problem);
    }

    private Result renderProblemTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        appendVersionLocalChangesWarning(template, problemService, problem);
        template.markBreadcrumbLocation(problem.getSlug(), routes.ProblemController.enterProblem(problem.getId()));

        template.addSecondaryTab("View", routes.ProblemController.viewProblem(problem.getId()));
        if (ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            template.addSecondaryTab("Update", routes.ProblemController.editProblem(problem.getId()));
        }

        return renderTemplate(template);
    }
}
