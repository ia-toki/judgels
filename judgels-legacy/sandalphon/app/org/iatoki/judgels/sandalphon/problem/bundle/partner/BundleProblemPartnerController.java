package org.iatoki.judgels.sandalphon.problem.bundle.partner;

import com.google.common.collect.ImmutableSet;
import judgels.jophiel.api.user.search.UserSearchService;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.*;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class BundleProblemPartnerController extends AbstractProblemController {

    private static final String PROBLEM = "problem";
    private static final String PARTNER = "partner";

    private final UserSearchService userSearchService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemPartnerController(UserSearchService userSearchService, ProblemService problemService) {
        this.userSearchService = userSearchService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = Form.form(ProblemPartnerUsernameForm.class);
        Form<ProblemPartnerUpsertForm> problemForm = Form.form(ProblemPartnerUpsertForm.class);
        Form<BundlePartnerUpsertForm> bundleForm = Form.form(BundlePartnerUpsertForm.class);

        return showAddPartner(usernameForm, problemForm, bundleForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = Form.form(ProblemPartnerUsernameForm.class).bindFromRequest();
        Form<ProblemPartnerUpsertForm> problemForm = Form.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<BundlePartnerUpsertForm> bundleForm = Form.form(BundlePartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showAddPartner(usernameForm, problemForm, bundleForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        BundlePartnerUpsertForm bundleData = bundleForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            usernameForm.reject("username", Messages.get("problem.partner.usernameNotFound"));
            return showAddPartner(usernameForm, problemForm, bundleForm, problem);
        }

        String userJid = usernameToJidMap.get(username);

        JidCacheServiceImpl.getInstance().putDisplayName(userJid, JudgelsPlayUtils.getUserDisplayName(username), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (problemService.isUserPartnerForProblem(problem.getJid(), userJid)) {
            usernameForm.reject("username", Messages.get("problem.partner.already"));
            return showAddPartner(usernameForm, problemForm, bundleForm, problem);
        }

        ProblemPartnerConfig problemConfig = new ProblemPartnerConfigBuilder()
              .setIsAllowedToUpdateProblem(problemData.isAllowedToUpdateProblem)
              .setIsAllowedToUpdateStatement(problemData.isAllowedToUpdateStatement)
              .setIsAllowedToUploadStatementResources(problemData.isAllowedToUploadStatementResources)
              .setAllowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToView))
              .setAllowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToUpdate))
              .setIsAllowedToManageStatementLanguages(problemData.isAllowedToManageStatementLanguages)
              .setIsAllowedToViewVersionHistory(problemData.isAllowedToViewVersionHistory)
              .setIsAllowedToRestoreVersionHistory(problemData.isAllowedToRestoreVersionHistory)
              .setIsAllowedToManageProblemClients(problemData.isAllowedToManageProblemClients)
              .build();

        BundleProblemPartnerConfig bundleConfig = new BundleProblemPartnerConfig(bundleData.isAllowedToSubmit, bundleData.isAllowedToManageItems);

        problemService.createProblemPartner(problem.getJid(), userJid, problemConfig, bundleConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(long problemId, long partnerId) throws ProblemNotFoundException, ProblemPartnerNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        ProblemPartner problemPartner = problemService.findProblemPartnerById(partnerId);

        ProblemPartnerConfig problemConfig = problemPartner.getBaseConfig();
        ProblemPartnerUpsertForm problemData = new ProblemPartnerUpsertForm();

        problemData.isAllowedToUpdateProblem = problemConfig.isAllowedToUpdateProblem();
        problemData.isAllowedToUpdateStatement = problemConfig.isAllowedToUpdateStatement();
        problemData.isAllowedToUploadStatementResources = problemConfig.isAllowedToUploadStatementResources();
        problemData.allowedStatementLanguagesToView = PartnerControllerUtils.combineByComma(problemConfig.getAllowedStatementLanguagesToView());
        problemData.allowedStatementLanguagesToUpdate = PartnerControllerUtils.combineByComma(problemConfig.getAllowedStatementLanguagesToUpdate());
        problemData.isAllowedToManageStatementLanguages = problemConfig.isAllowedToManageStatementLanguages();
        problemData.isAllowedToViewVersionHistory = problemConfig.isAllowedToViewVersionHistory();
        problemData.isAllowedToRestoreVersionHistory = problemConfig.isAllowedToRestoreVersionHistory();
        problemData.isAllowedToManageProblemClients = problemConfig.isAllowedToManageProblemClients();

        Form<ProblemPartnerUpsertForm> problemForm = Form.form(ProblemPartnerUpsertForm.class).fill(problemData);

        BundleProblemPartnerConfig bundleConfig = problemPartner.getChildConfig(BundleProblemPartnerConfig.class);
        BundlePartnerUpsertForm bundleData = new BundlePartnerUpsertForm();

        bundleData.isAllowedToManageItems = bundleConfig.isAllowedToManageItems();

        Form<BundlePartnerUpsertForm> bundleForm = Form.form(BundlePartnerUpsertForm.class).fill(bundleData);

        return showEditPartner(problemForm, bundleForm, problem, problemPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(long problemId, long partnerId) throws ProblemNotFoundException, ProblemPartnerNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        ProblemPartner problemPartner = problemService.findProblemPartnerById(partnerId);

        Form<ProblemPartnerUpsertForm> problemForm = Form.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<BundlePartnerUpsertForm> bundleForm = Form.form(BundlePartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(problemForm) || formHasErrors(bundleForm)) {
            return showEditPartner(problemForm, bundleForm, problem, problemPartner);
        }

        ProblemPartnerUpsertForm problemData = problemForm.get();

        ProblemPartnerConfig problemConfig = new ProblemPartnerConfigBuilder()
              .setIsAllowedToUpdateProblem(problemData.isAllowedToUpdateProblem)
              .setIsAllowedToUpdateStatement(problemData.isAllowedToUpdateStatement)
              .setIsAllowedToUploadStatementResources(problemData.isAllowedToUploadStatementResources)
              .setAllowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToView))
              .setAllowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(problemData.allowedStatementLanguagesToUpdate))
              .setIsAllowedToManageStatementLanguages(problemData.isAllowedToManageStatementLanguages)
              .setIsAllowedToViewVersionHistory(problemData.isAllowedToViewVersionHistory)
              .setIsAllowedToRestoreVersionHistory(problemData.isAllowedToRestoreVersionHistory)
              .setIsAllowedToManageProblemClients(problemData.isAllowedToManageProblemClients)
              .build();

        BundlePartnerUpsertForm bundleData = bundleForm.get();

        BundleProblemPartnerConfig bundleConfig = new BundleProblemPartnerConfig(bundleData.isAllowedToSubmit, bundleData.isAllowedToManageItems);

        problemService.updateProblemPartner(partnerId, problemConfig, bundleConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
    }

    private Result showAddPartner(Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addPartnerView.render(usernameForm, problemForm, bundleForm, problem, JophielClientControllerUtils.getInstance().getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle(Messages.get("problem.partner.add"));
        template.markBreadcrumbLocation(Messages.get("problem.partner.add"), routes.BundleProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Add Partner");

        return renderPartnerTemplate(template, problemService, problem);
    }

    private Result showEditPartner(Form<ProblemPartnerUpsertForm> problemForm, Form<BundlePartnerUpsertForm> bundleForm, Problem problem, ProblemPartner problemPartner) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(problemForm, bundleForm, problem, problemPartner));

        template.setSecondaryTitle(Messages.get("problem.partner.update") + ": " + JidCacheServiceImpl.getInstance().getDisplayName(problemPartner.getPartnerJid()));
        template.markBreadcrumbLocation(Messages.get("problem.partner.update"), routes.BundleProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
        template.setPageTitle("Problem - Update Partner");

        return renderPartnerTemplate(template, problemService, problem);
    }
}
