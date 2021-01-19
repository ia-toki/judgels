package org.iatoki.judgels.sandalphon.problem.bundle.partner;

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
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUpsertForm;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUsernameForm;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class BundleProblemPartnerController extends AbstractProblemController {
    private final UserSearchService userSearchService;
    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public BundleProblemPartnerController(UserSearchService userSearchService, ProblemService problemService, ProfileService profileService) {
        this.userSearchService = userSearchService;
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class);
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class);
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class);

        return showAddPartner(usernameForm, problemForm, bundleForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class).bindFromRequest();
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showAddPartner(usernameForm, problemForm, bundleForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        BundlePartnerUpsertForm bundleData = bundleForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(usernameForm.withError("username", "Username not found."), problemForm, bundleForm, problem);
        }

        String userJid = usernameToJidMap.get(username);
        if (problemService.isUserPartnerForProblem(problem.getJid(), userJid)) {
            return showAddPartner(usernameForm.withError("username", "This user is already a partner."), problemForm, bundleForm, problem);
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

        problemService.createProblemPartner(problem.getJid(), userJid, problemConfig, bundleConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(long problemId, long partnerId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        ProblemPartner problemPartner = checkFound(problemService.findProblemPartnerById(partnerId));

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

        return showEditPartner(problemForm, bundleForm, problem, problemPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(long problemId, long partnerId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        ProblemPartner problemPartner = checkFound(problemService.findProblemPartnerById(partnerId));

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<BundlePartnerUpsertForm> bundleForm = formFactory.form(BundlePartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showEditPartner(problemForm, bundleForm, problem, problemPartner);
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

        problemService.updateProblemPartner(partnerId, problemConfig, bundleConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
    }

    private Result showAddPartner(Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addPartnerView.render(usernameForm, problemForm, bundleForm, problem, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.BundleProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Add Partner");

        return renderPartnerTemplate(template, problemService, problem);
    }

    private Result showEditPartner(Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem, ProblemPartner problemPartner) {
        Profile profile = profileService.getProfile(problemPartner.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(problemForm, bundleForm, problem, problemPartner));

        template.setSecondaryTitle("Update partner: " + profile.getUsername());
        template.markBreadcrumbLocation("Update partner", routes.BundleProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
        template.setPageTitle("Problem - Update partner");

        return renderPartnerTemplate(template, problemService, problem);
    }
}
