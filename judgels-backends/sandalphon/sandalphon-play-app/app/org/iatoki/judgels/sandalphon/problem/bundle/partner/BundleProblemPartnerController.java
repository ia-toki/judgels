package org.iatoki.judgels.sandalphon.problem.bundle.partner;

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
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class BundleProblemPartnerController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final UserSearchService userSearchService;
    private final ProfileService profileService;

    @Inject
    public BundleProblemPartnerController(
            UserSearchService userSearchService,
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
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
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class);

        return showAddPartner(req, usernameForm, problemForm, bundleForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(Http.Request req, long problemId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class).bindFromRequest(req);
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest(req);
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showAddPartner(req, usernameForm, problemForm, bundleForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        BundlePartnerUpsertForm bundleData = bundleForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(req, usernameForm.withError("username", "Username not found."), problemForm, bundleForm, problem);
        }

        String userJid = usernameToJidMap.get(username);
        if (problemStore.isUserPartnerForProblem(problem.getJid(), userJid)) {
            return showAddPartner(req, usernameForm.withError("username", "This user is already a partner."), problemForm, bundleForm, problem);
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

        ProblemPartnerChildConfig bundleConfig = new ProblemPartnerChildConfig.Builder()
                .isAllowedToSubmit(bundleData.isAllowedToSubmit)
                .isAllowedToManageItems(bundleData.isAllowedToManageItems)
                .build();

        problemStore.createProblemPartner(problem.getJid(), userJid, problemConfig, bundleConfig);

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

        ProblemPartnerChildConfig bundleConfig = problemPartner.getChildConfig();
        BundlePartnerUpsertForm bundleData = new BundlePartnerUpsertForm();

        bundleData.isAllowedToSubmit = bundleConfig.getIsAllowedToSubmit();
        bundleData.isAllowedToManageItems = bundleConfig.getIsAllowedToManageItems();

        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class).fill(bundleData);

        return showEditPartner(req, problemForm, bundleForm, problem, problemPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(Http.Request req, long problemId, long partnerId) {
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAuthorOrAbove(req, problem));

        ProblemPartner problemPartner = checkFound(problemStore.findProblemPartnerById(partnerId));

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest(req);
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showEditPartner(req, problemForm, bundleForm, problem, problemPartner);
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

        BundlePartnerUpsertForm bundleData = bundleForm.get();

        ProblemPartnerChildConfig bundleConfig = new ProblemPartnerChildConfig.Builder()
                .isAllowedToSubmit(bundleData.isAllowedToSubmit)
                .isAllowedToManageItems(bundleData.isAllowedToManageItems)
                .build();

        problemStore.updateProblemPartner(partnerId, problemConfig, bundleConfig);

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problem.getId()));
    }

    private Result showAddPartner(Http.Request req, Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(addPartnerView.render(usernameForm, problemForm, bundleForm, problem, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.BundleProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Add Partner");

        return renderPartnerTemplate(template, problem);
    }

    private Result showEditPartner(Http.Request req, Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem, ProblemPartner problemPartner) {
        Profile profile = profileService.getProfile(problemPartner.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(editPartnerView.render(problemForm, bundleForm, problem, problemPartner));

        template.setSecondaryTitle("Update partner: " + profile.getUsername());
        template.markBreadcrumbLocation("Update partner", routes.BundleProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
        template.setPageTitle("Problem - Update partner");

        return renderPartnerTemplate(template, problem);
    }
}
