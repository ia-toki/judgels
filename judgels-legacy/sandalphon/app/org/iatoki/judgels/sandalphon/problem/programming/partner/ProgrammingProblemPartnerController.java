package org.iatoki.judgels.sandalphon.problem.programming.partner;

import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartner;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfigBuilder;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUpsertForm;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUsernameForm;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ProgrammingProblemPartnerController extends AbstractJudgelsController {

    private static final String PROBLEM = "problem";
    private static final String PARTNER = "partner";

    private final JophielPublicAPI jophielPublicAPI;
    private final ProblemService problemService;

    @Inject
    public ProgrammingProblemPartnerController(JophielPublicAPI jophielPublicAPI, ProblemService problemService) {
        this.jophielPublicAPI = jophielPublicAPI;
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
        Form<ProgrammingPartnerUpsertForm> programmingForm = Form.form(ProgrammingPartnerUpsertForm.class);

        return showAddPartner(usernameForm, problemForm, programmingForm, problem);
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
        Form<ProgrammingPartnerUpsertForm> programmingForm = Form.form(ProgrammingPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(programmingForm)) {
            return showAddPartner(usernameForm, problemForm, programmingForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        ProgrammingPartnerUpsertForm programmingData = programmingForm.get();

        JophielUser jophielUser = jophielPublicAPI.findUserByUsername(username);

        if (jophielUser == null) {
            usernameForm.reject("username", Messages.get("problem.partner.usernameNotFound"));
            return showAddPartner(usernameForm, problemForm, programmingForm, problem);
        }

        JidCacheServiceImpl.getInstance().putDisplayName(jophielUser.getJid(), JudgelsPlayUtils.getUserDisplayName(jophielUser.getUsername()), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (problemService.isUserPartnerForProblem(problem.getJid(), jophielUser.getJid())) {
            usernameForm.reject("username", Messages.get("problem.partner.already"));
            return showAddPartner(usernameForm, problemForm, programmingForm, problem);
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

        ProgrammingProblemPartnerConfig programmingConfig = new ProgrammingProblemPartnerConfigBuilder()
              .setIsAllowedToSubmit(programmingData.isAllowedToSubmit)
              .setIsAllowedToManageGrading(programmingData.isAllowedToManageGrading)
              .build();

        problemService.createProblemPartner(problem.getJid(), jophielUser.getJid(), problemConfig, programmingConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(PROBLEM, problem.getJid(), problem.getSlug(), PARTNER, jophielUser.getJid(), jophielUser.getUsername()));

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

        ProgrammingProblemPartnerConfig programmingConfig = problemPartner.getChildConfig(ProgrammingProblemPartnerConfig.class);
        ProgrammingPartnerUpsertForm programmingData = new ProgrammingPartnerUpsertForm();

        programmingData.isAllowedToSubmit = programmingConfig.isAllowedToSubmit();
        programmingData.isAllowedToManageGrading = programmingConfig.isAllowedToManageGrading();

        Form<ProgrammingPartnerUpsertForm> programmingForm = Form.form(ProgrammingPartnerUpsertForm.class).fill(programmingData);

        return showEditPartner(problemForm, programmingForm, problem, problemPartner);
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
        Form<ProgrammingPartnerUpsertForm> programmingForm = Form.form(ProgrammingPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(problemForm) || formHasErrors(programmingForm)) {
            return showEditPartner(problemForm, programmingForm, problem, problemPartner);
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

        ProgrammingPartnerUpsertForm programmingData = programmingForm.get();

        ProgrammingProblemPartnerConfig programmingConfig = new ProgrammingProblemPartnerConfigBuilder()
                .setIsAllowedToSubmit(programmingData.isAllowedToSubmit)
                .setIsAllowedToManageGrading(programmingData.isAllowedToManageGrading)
                .build();


        problemService.updateProblemPartner(partnerId, problemConfig, programmingConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(PROBLEM, problem.getJid(), problem.getSlug(), PARTNER, problemPartner.getPartnerJid(), JidCacheServiceImpl.getInstance().getDisplayName(problemPartner.getPartnerJid())));

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
    }

    private Result showAddPartner(Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem) {
        LazyHtml content = new LazyHtml(addPartnerView.render(usernameForm, problemForm, programmingForm, problem, jophielPublicAPI.getUserAutocompleteAPIEndpoint()));

        content.appendLayout(c -> heading3Layout.render(Messages.get("problem.partner.add"), c));
        ProgrammingProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        ProblemPartnerControllerUtils.appendBreadcrumbsLayout(content, problem, new InternalLink(Messages.get("problem.partner.add"), routes.ProgrammingProblemPartnerController.addPartner(problem.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Add Partner");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditPartner(Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem, ProblemPartner problemPartner) {
        LazyHtml content = new LazyHtml(editPartnerView.render(problemForm, programmingForm, problem, problemPartner));

        content.appendLayout(c -> heading3Layout.render(Messages.get("problem.partner.update") + ": " + JidCacheServiceImpl.getInstance().getDisplayName(problemPartner.getPartnerJid()), c));
        ProgrammingProblemControllerUtils.appendTabsLayout(content, problemService, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        ProblemControllerUtils.appendTitleLayout(content, problemService, problem);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        ProblemPartnerControllerUtils.appendBreadcrumbsLayout(content, problem, new InternalLink(Messages.get("problem.partner.update"), routes.ProgrammingProblemPartnerController.editPartner(problem.getId(), problemPartner.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Update Partner");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }
}
