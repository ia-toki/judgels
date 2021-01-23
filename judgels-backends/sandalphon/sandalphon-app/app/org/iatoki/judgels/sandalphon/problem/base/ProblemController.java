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
import org.iatoki.judgels.play.actor.ActorChecker;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.SandalphonSessionUtils;
import org.iatoki.judgels.sandalphon.problem.base.html.createProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.editProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.listProblemsView;
import org.iatoki.judgels.sandalphon.problem.base.html.viewProblemView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProblemController extends AbstractBaseProblemController {

    private static final long PAGE_SIZE = 20;

    private final ActorChecker actorChecker;
    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public ProblemController(ActorChecker actorChecker, ProblemService problemService, ProfileService profileService) {
        super(problemService);
        this.actorChecker = actorChecker;
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result index(Http.Request req) {
        return listProblems(req, 0, "updatedAt", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listProblems(Http.Request req, long pageIndex, String sortBy, String orderBy, String filterString) {
        String actorJid = actorChecker.check(req);

        boolean isAdmin = SandalphonControllerUtils.getInstance().isAdmin();
        boolean isWriter = SandalphonControllerUtils.getInstance().isWriter();
        Page<Problem> pageOfProblems = problemService.getPageOfProblems(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, actorJid, isAdmin);

        Set<String> userJids = pageOfProblems.getPage().stream().map(Problem::getAuthorJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
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
    public Result createProblem(Http.Request req) {
        actorChecker.check(req);

        Form<ProblemCreateForm> problemCreateForm = formFactory.form(ProblemCreateForm.class);

        return showCreateProblem(req, problemCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProblem(Http.Request req) {
        actorChecker.check(req);

        Form<ProblemCreateForm> problemCreateForm = formFactory.form(ProblemCreateForm.class).bindFromRequest(req);

        if (formHasErrors(problemCreateForm)) {
            return showCreateProblem(req, problemCreateForm);
        }

        if (problemService.problemExistsBySlug(problemCreateForm.get().slug)) {
            return showCreateProblem(req, problemCreateForm.withError("slug", "Slug already exists"));
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
    public Result viewProblem(Http.Request req, long problemId) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        if (!ProblemControllerUtils.isAllowedToViewStatement(problemService, problem, language)) {
            return notFound();
        }

        Profile profile = profileService.getProfile(problem.getAuthorJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewProblemView.render(problem, profile));
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());
        template.addMainButton("Enter problem", routes.ProblemController.enterProblem(problem.getId()));
        template.markBreadcrumbLocation("View problem", routes.ProblemController.viewProblem(problem.getId()));
        template.setPageTitle("Problem - View");
        return renderProblemTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblem(Http.Request req, long problemId) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return redirect(routes.ProblemController.viewProblem(problem.getId()));
        }

        ProblemEditForm problemEditData = new ProblemEditForm();
        problemEditData.slug = problem.getSlug();
        problemEditData.additionalNote = problem.getAdditionalNote();

        Form<ProblemEditForm> problemEditForm = formFactory.form(ProblemEditForm.class).fill(problemEditData);

        return showEditProblem(req, problemEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditProblem(Http.Request req, long problemId) {
        actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return notFound();
        }

        Form<ProblemEditForm> problemEditForm = formFactory.form(ProblemEditForm.class).bindFromRequest(req);

        if (formHasErrors(problemEditForm)) {
            return showEditProblem(req, problemEditForm, problem);
        }

        if (!problem.getSlug().equals(problemEditForm.get().slug) && problemService.problemExistsBySlug(problemEditForm.get().slug)) {
            return showEditProblem(req, problemEditForm.withError("slug", "Slug already exists"), problem);
        }

        ProblemEditForm problemEditData = problemEditForm.get();
        problemService.updateProblem(problem.getJid(), problemEditData.slug, problemEditData.additionalNote);

        return redirect(routes.ProblemController.viewProblem(problem.getId()));
    }

    @RequireCSRFCheck
    public Result switchLanguage(Http.Request req, long problemId) {
        String language = formFactory.form().bindFromRequest(req).get("langCode");

        return redirect(req.getHeaders().get("Referer").orElse(""))
                .addingToSession(req, SandalphonSessionUtils.newCurrentStatementLanguage(language));
    }

    private Result showCreateProblem(Http.Request req, Form<ProblemCreateForm> problemCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(createProblemView.render(problemCreateForm));
        template.setMainTitle("Create problem");
        template.markBreadcrumbLocation("Create problem", routes.ProblemController.createProblem());
        template.setPageTitle("Problem - Create");
        return renderTemplate(template);
    }

    private Result showEditProblem(Http.Request req, Form<ProblemEditForm> problemEditForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
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
