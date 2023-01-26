package org.iatoki.judgels.sandalphon.problem.programming.partner;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.partner.ProblemPartner;
import judgels.sandalphon.api.problem.partner.ProblemPartnerChildConfig;
import judgels.sandalphon.api.problem.partner.ProblemPartnerConfig;
import judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUpsertForm;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUsernameForm;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemPartnerController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public ProgrammingProblemPartnerController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            UserSearchService userSearchService,
            ProfileService profileService) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.userSearchService = userSearchService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class);
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class);
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class);

        return showAddPartner(req, usernameForm, problemForm, programmingForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class).bindFromRequest(req);
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest(req);
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(programmingForm)) {
            return showAddPartner(req, usernameForm, problemForm, programmingForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        ProgrammingPartnerUpsertForm programmingData = programmingForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(req, usernameForm.withError("username", "Username not found."), problemForm, programmingForm, problem);
        }

        String userJid = usernameToJidMap.get(username);
        if (problemStore.isUserPartnerForProblem(problem.getJid(), userJid)) {
            return showAddPartner(req, usernameForm.withError("username", "This user is already a partner."), problemForm, programmingForm, problem);
        }

        ProblemPartnerConfig problemConfig = new ProblemPartnerConfig.Builder()
                .isAllowedToUpdateProblem(problemData.isAllowedToUpdateProblem)
                .isAllowedToUpdateStatement(problemData.isAllowedToUpdateStatement)
                .isAllowedToUploadStatementResources(problemData.isAllowedToUploadStatementResources)
                .allowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToView))
                .allowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToUpdate))
                .isAllowedToManageStatementLanguages(problemData.isAllowedToManageStatementLanguages)
                .isAllowedToViewVersionHistory(problemData.isAllowedToViewVersionHistory)
                .isAllowedToRestoreVersionHistory(problemData.isAllowedToRestoreVersionHistory)
                .build();

        ProblemPartnerChildConfig programmingConfig = new ProblemPartnerChildConfig.Builder()
                .isAllowedToSubmit(programmingData.isAllowedToSubmit)
                .isAllowedToManageGrading(programmingData.isAllowedToManageGrading)
                .build();

        problemStore.createProblemPartner(problem.getJid(), userJid, problemConfig, programmingConfig);

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(Http.Request req, long problemId, long partnerId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        ProblemPartner problemPartner = checkFound(problemStore.findProblemPartnerById(partnerId));

        ProblemPartnerConfig problemConfig = problemPartner.getBaseConfig();
        ProblemPartnerUpsertForm problemData = new ProblemPartnerUpsertForm();

        problemData.isAllowedToUpdateProblem = problemConfig.getIsAllowedToUpdateProblem();
        problemData.isAllowedToUpdateStatement = problemConfig.getIsAllowedToUpdateStatement();
        problemData.isAllowedToUploadStatementResources = problemConfig.getIsAllowedToUploadStatementResources();
        problemData.allowedStatementLanguagesToView = PartnerControllerUtils.combineByComma(problemConfig.getAllowedStatementLanguagesToView());
        problemData.allowedStatementLanguagesToUpdate = PartnerControllerUtils.combineByComma(problemConfig.getAllowedStatementLanguagesToUpdate());
        problemData.isAllowedToManageStatementLanguages = problemConfig.getIsAllowedToManageStatementLanguages();
        problemData.isAllowedToViewVersionHistory = problemConfig.getIsAllowedToViewVersionHistory();
        problemData.isAllowedToRestoreVersionHistory = problemConfig.getIsAllowedToRestoreVersionHistory();

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).fill(problemData);

        ProblemPartnerChildConfig programmingConfig = problemPartner.getChildConfig();
        ProgrammingPartnerUpsertForm programmingData = new ProgrammingPartnerUpsertForm();

        programmingData.isAllowedToSubmit = programmingConfig.getIsAllowedToSubmit();
        programmingData.isAllowedToManageGrading = programmingConfig.getIsAllowedToManageGrading();

        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).fill(programmingData);

        return showEditPartner(req, problemForm, programmingForm, problem, problemPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(Http.Request req, long problemId, long partnerId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        ProblemPartner problemPartner = checkFound(problemStore.findProblemPartnerById(partnerId));

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest(req);
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(problemForm) || formHasErrors(programmingForm)) {
            return showEditPartner(req, problemForm, programmingForm, problem, problemPartner);
        }

        ProblemPartnerUpsertForm problemData = problemForm.get();

        ProblemPartnerConfig problemConfig = new ProblemPartnerConfig.Builder()
                .isAllowedToUpdateProblem(problemData.isAllowedToUpdateProblem)
                .isAllowedToUpdateStatement(problemData.isAllowedToUpdateStatement)
                .isAllowedToUploadStatementResources(problemData.isAllowedToUploadStatementResources)
                .allowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToView))
                .allowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToUpdate))
                .isAllowedToManageStatementLanguages(problemData.isAllowedToManageStatementLanguages)
                .isAllowedToViewVersionHistory(problemData.isAllowedToViewVersionHistory)
                .isAllowedToRestoreVersionHistory(problemData.isAllowedToRestoreVersionHistory)
                .build();

        ProgrammingPartnerUpsertForm programmingData = programmingForm.get();

        ProblemPartnerChildConfig programmingConfig = new ProblemPartnerChildConfig.Builder()
                .isAllowedToSubmit(programmingData.isAllowedToSubmit)
                .isAllowedToManageGrading(programmingData.isAllowedToManageGrading)
                .build();


        problemStore.updateProblemPartner(partnerId, problemConfig, programmingConfig);

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problem.getId()));
    }

    private Result showAddPartner(Http.Request req, Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(addPartnerView.render(usernameForm, problemForm, programmingForm, problem, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.ProgrammingProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Add partner");

        return renderPartnerTemplate(template, problem);
    }

    private Result showEditPartner(Http.Request req, Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem, ProblemPartner problemPartner) {
        Profile profile = profileService.getProfile(problemPartner.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editPartnerView.render(problemForm, programmingForm, problem, problemPartner));

        template.setSecondaryTitle("Update partner: " + profile.getUsername());
        template.markBreadcrumbLocation("Update partner", routes.ProgrammingProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
        template.setPageTitle("Problem - Update partner");

        return renderPartnerTemplate(template, problem);
    }
}
