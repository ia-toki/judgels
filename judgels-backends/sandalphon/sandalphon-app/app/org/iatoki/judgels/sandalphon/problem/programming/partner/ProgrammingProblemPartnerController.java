package org.iatoki.judgels.sandalphon.problem.programming.partner;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartner;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfig;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerConfigBuilder;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUpsertForm;
import org.iatoki.judgels.sandalphon.problem.base.partner.ProblemPartnerUsernameForm;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.problem.programming.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemPartnerController extends AbstractProblemController {
    private final UserSearchService userSearchService;
    private final ProfileService profileService;
    private final ProblemService problemService;

    @Inject
    public ProgrammingProblemPartnerController(UserSearchService userSearchService, ProfileService profileService, ProblemService problemService) {
        this.userSearchService = userSearchService;
        this.profileService = profileService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class);
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class);
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class);

        return showAddPartner(usernameForm, problemForm, programmingForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return notFound();
        }

        Form<ProblemPartnerUsernameForm> usernameForm = formFactory.form(ProblemPartnerUsernameForm.class).bindFromRequest();
        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(problemForm) || formHasErrors(programmingForm)) {
            return showAddPartner(usernameForm, problemForm, programmingForm, problem);
        }

        String username = usernameForm.get().username;
        ProblemPartnerUpsertForm problemData = problemForm.get();
        ProgrammingPartnerUpsertForm programmingData = programmingForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(usernameForm.withError("username", "Username not found."), problemForm, programmingForm, problem);
        }

        String userJid = usernameToJidMap.get(username);

        JidCacheServiceImpl.getInstance().putDisplayName(userJid, JudgelsPlayUtils.getUserDisplayName(username), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (problemService.isUserPartnerForProblem(problem.getJid(), userJid)) {
            return showAddPartner(usernameForm.withError("username", "This user is already a partner."), problemForm, programmingForm, problem);
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

        problemService.createProblemPartner(problem.getJid(), userJid, problemConfig, programmingConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

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

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).fill(problemData);

        ProgrammingProblemPartnerConfig programmingConfig = problemPartner.getChildConfig(ProgrammingProblemPartnerConfig.class);
        ProgrammingPartnerUpsertForm programmingData = new ProgrammingPartnerUpsertForm();

        programmingData.isAllowedToSubmit = programmingConfig.isAllowedToSubmit();
        programmingData.isAllowedToManageGrading = programmingConfig.isAllowedToManageGrading();

        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).fill(programmingData);

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

        Form<ProblemPartnerUpsertForm> problemForm = formFactory.form(ProblemPartnerUpsertForm.class).bindFromRequest();
        Form<ProgrammingPartnerUpsertForm> programmingForm = formFactory.form(ProgrammingPartnerUpsertForm.class).bindFromRequest();

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

        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
    }

    private Result showAddPartner(Form<ProblemPartnerUsernameForm> usernameForm, Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addPartnerView.render(usernameForm, problemForm, programmingForm, problem, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.ProgrammingProblemPartnerController.addPartner(problem.getId()));
        template.setPageTitle("Problem - Add partner");

        return renderPartnerTemplate(template, problemService, problem);
    }

    private Result showEditPartner(Form<ProblemPartnerUpsertForm> problemForm, Form<ProgrammingPartnerUpsertForm> programmingForm, Problem problem, ProblemPartner problemPartner) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(problemForm, programmingForm, problem, problemPartner));

        template.setSecondaryTitle("Update partner: " + JidCacheServiceImpl.getInstance().getDisplayName(problemPartner.getPartnerJid()));
        template.markBreadcrumbLocation("Update partner", routes.ProgrammingProblemPartnerController.editPartner(problem.getId(), problemPartner.getId()));
        template.setPageTitle("Problem - Update partner");

        return renderPartnerTemplate(template, problemService, problem);
    }
}
