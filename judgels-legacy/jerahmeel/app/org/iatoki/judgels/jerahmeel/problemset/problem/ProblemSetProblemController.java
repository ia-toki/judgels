package org.iatoki.judgels.jerahmeel.problemset.problem;

import com.google.common.collect.ImmutableSet;
import com.palantir.conjure.java.api.errors.RemoteException;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.service.api.client.BasicAuthHeader;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.problemset.AbstractProblemSetController;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetNotFoundException;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.addProblemSetProblemView;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.editProblemSetProblemView;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.listProblemSetProblemsView;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.listVisibleProblemSetProblemsView;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.listVisibleProblemSetProblemsWithScoreView;
import org.iatoki.judgels.jerahmeel.problemset.problem.html.viewProblemSetProblemView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonResourceDisplayNames;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ProblemSetProblemController extends AbstractProblemSetController {

    private static final long PAGE_SIZE = 20;
    private static final String PROBLEM = "problem";
    private static final String PROBLEM_SET = "problem set";

    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;
    private final ProblemSetService problemSetService;
    private final SandalphonClientAPI sandalphonClientAPI;
    private final ProblemSetProblemService problemSetProblemService;

    @Inject
    public ProblemSetProblemController(@Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader, ClientProblemService clientProblemService, ProblemSetService problemSetService, SandalphonClientAPI sandalphonClientAPI, ProblemSetProblemService problemSetProblemService) {
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
        this.problemSetService = problemSetService;
        this.sandalphonClientAPI = sandalphonClientAPI;
        this.problemSetProblemService = problemSetProblemService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewVisibleProblemSetProblems(long problemSetId) throws ProblemSetNotFoundException {
        return listVisibleProblemSetProblems(problemSetId, 0, "alias", "asc", "");
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result listVisibleProblemSetProblems(long problemSetId, long page, String orderBy, String orderDir, String filterString) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        HtmlTemplate template = getBaseHtmlTemplate();
        if (!JerahmeelUtils.isGuest()) {
            Page<ProblemSetProblemWithScore> pageOfProblemSetProblemsWithScore = problemSetProblemService.getPageOfProblemSetProblemsWithScore(IdentityUtils.getUserJid(), problemSet.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> problemJids = pageOfProblemSetProblemsWithScore.getData().stream().map(cp -> cp.getProblemSetProblem().getProblemJid()).collect(Collectors.toList());
            Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), StatementControllerUtils.getCurrentStatementLanguage());

            template.setContent(listVisibleProblemSetProblemsWithScoreView.render(problemSet, pageOfProblemSetProblemsWithScore, orderBy, orderDir, filterString, problemTitlesMap));
        } else {
            Page<ProblemSetProblem> pageOfProblemSetProblems = problemSetProblemService.getPageOfProblemSetProblems(problemSet.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
            List<String> problemJids = pageOfProblemSetProblems.getData().stream().map(cp -> cp.getProblemJid()).collect(Collectors.toList());
            Map<String, String> problemTitlesMap = SandalphonResourceDisplayNameUtils.buildTitlesMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids), StatementControllerUtils.getCurrentStatementLanguage());

            template.setContent(listVisibleProblemSetProblemsView.render(problemSet, pageOfProblemSetProblems, orderBy, orderDir, filterString, problemTitlesMap));
        }

        if (JerahmeelUtils.hasRole("admin")) {
            appendProblemSubtabs(template, problemSet);
        }
        template.markBreadcrumbLocation(Messages.get("commons.view"), routes.ProblemSetProblemController.viewVisibleProblemSetProblems(problemSet.getId()));
        template.setPageTitle("Problem Sets - Problems");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result viewProblemSetProblem(long problemSetId, long problemSetProblemId) throws ProblemSetNotFoundException, ProblemSetProblemNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemById(problemSetProblemId);

        if ((!JerahmeelUtils.hasRole("admin") && (problemSetProblem.getStatus() != ProblemSetProblemStatus.VISIBLE) || !problemSetProblem.getProblemSetJid().equals(problemSet.getJid()))) {
            return notFound();
        }

        if (!isAdmin()) {
            return Results.movedPermanently("https://tlx.toki.id/problems/" + problemSet.getSlug() + "/" + problemSetProblem.getAlias());
        }

        String reasonNotAllowedToSubmit = null;
        if (JerahmeelUtils.isGuest()) {
            reasonNotAllowedToSubmit = Messages.get("archive.problemSet.problem.mustLogin");
        }

        String requestUrl;
        String requestBody;

        if (ProblemSetProblemType.BUNDLE.equals(problemSetProblem.getType())) {
            SandalphonBundleProblemStatementRenderRequestParam param = new SandalphonBundleProblemStatementRenderRequestParam();

            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(routes.ProblemSetProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.problemset.submission.bundle.routes.ProblemSetBundleSubmissionController.postSubmitProblem(problemSet.getId(), problemSetProblem.getProblemJid()).absoluteURL(request(), request().secure()));
            param.setReasonNotAllowedToSubmit(reasonNotAllowedToSubmit);

            requestUrl = sandalphonClientAPI.getBundleProblemStatementRenderAPIEndpoint(problemSetProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructBundleProblemStatementRenderAPIRequestBody(problemSetProblem.getProblemJid(), param);
        } else if (ProblemSetProblemType.PROGRAMMING.equals(problemSetProblem.getType())) {
            SandalphonProgrammingProblemStatementRenderRequestParam param = new SandalphonProgrammingProblemStatementRenderRequestParam();

            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(routes.ProblemSetProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.problemset.submission.programming.routes.ProblemSetProgrammingSubmissionController.postSubmitProblem(problemSet.getId(), problemSetProblem.getProblemJid()).absoluteURL(request(), request().secure()));
            param.setReasonNotAllowedToSubmit(reasonNotAllowedToSubmit);
            param.setAllowedGradingLanguages("");

            requestUrl = sandalphonClientAPI.getProgrammingProblemStatementRenderAPIEndpoint(problemSetProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructProgrammingProblemStatementRenderAPIRequestBody(problemSetProblem.getProblemJid(), param);
        } else {
            throw new IllegalStateException();
        }

        session("problemJid", problemSetProblem.getProblemJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewProblemSetProblemView.render(requestUrl, requestBody));
        template.markBreadcrumbLocation(problemSetProblem.getAlias(), routes.ProblemSetProblemController.viewProblemSetProblem(problemSet.getId(), problemSetProblem.getId()));

        template.setPageTitle("Problem Sets - Problem");

        return renderTemplate(template, problemSet);
    }

    @Authenticated(value = GuestView.class)
    public Result switchLanguage() {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        StatementControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    @Authenticated(value = GuestView.class)
    @Transactional(readOnly = true)
    public Result renderProblemSetProblemMedia(long problemSetId, long problemSetProblemId, String filename) throws ProblemSetNotFoundException, ProblemSetProblemNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemById(problemSetProblemId);

        if ((!JerahmeelUtils.hasRole("admin") && (problemSetProblem.getStatus() != ProblemSetProblemStatus.VISIBLE) || !problemSetProblem.getProblemSetJid().equals(problemSet.getJid()))) {
            return notFound();
        }

        String mediaUrl = sandalphonClientAPI.getProblemStatementMediaRenderAPIEndpoint(problemSetProblem.getProblemJid(), filename);

        return redirect(mediaUrl);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    public Result viewProblemSetProblems(long problemSetId) throws ProblemSetNotFoundException {
        return listProblemSetProblems(problemSetId, 0, "alias", "asc", "");
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    public Result listProblemSetProblems(long problemSetId, long page, String orderBy, String orderDir, String filterString) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        Page<ProblemSetProblem> pageOfProblemSetProblems = problemSetProblemService.getPageOfProblemSetProblems(problemSet.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        List<String> problemJids = pageOfProblemSetProblems.getData().stream().map(cp -> cp.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemSlugsMap = SandalphonResourceDisplayNameUtils.buildSlugsMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids));

        return showListProblemSetProblems(problemSet, pageOfProblemSetProblems, orderBy, orderDir, filterString, problemSlugsMap);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addProblemSetProblem(long problemSetId) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        Form<ProblemSetProblemAddForm> problemSetProblemAddForm = Form.form(ProblemSetProblemAddForm.class);

        return showAddProblemSetProblem(problemSet, problemSetProblemAddForm);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postAddProblemSetProblem(long problemSetId) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        Form<ProblemSetProblemAddForm> problemSetProblemAddForm = Form.form(ProblemSetProblemAddForm.class).bindFromRequest();

        if (formHasErrors(problemSetProblemAddForm)) {
            return showAddProblemSetProblem(problemSet, problemSetProblemAddForm);
        }

        ProblemSetProblemAddForm problemSetProblemAddData = problemSetProblemAddForm.get();

        if (problemSetProblemService.aliasExistsInProblemSet(problemSet.getJid(), problemSetProblemAddData.alias)) {
            problemSetProblemAddForm.reject(Messages.get("error.problemSet.problem.duplicateAlias"));

            return showAddProblemSetProblem(problemSet, problemSetProblemAddForm);
        }

        Map<String, String> problemSlugToJidMap = clientProblemService.translateAllowedSlugsToJids(sandalphonClientAuthHeader, IdentityUtils.getUserJid(), ImmutableSet.of(problemSetProblemAddData.problemSlug));
        if (!problemSlugToJidMap.containsKey(problemSetProblemAddData.problemSlug)) {
            problemSetProblemAddForm.reject(Messages.get("error.problem.invalid"));
            return showAddProblemSetProblem(problemSet, problemSetProblemAddForm);
        }

        String problemJid = problemSlugToJidMap.get(problemSetProblemAddData.problemSlug);
        ProblemInfo problem;
        try {
            problem = clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);
        } catch (RemoteException e) {
            if (e.getStatus() >= Http.Status.INTERNAL_SERVER_ERROR) {
                problemSetProblemAddForm.reject(Messages.get("error.system.sandalphon.connection"));
            } else {
                problemSetProblemAddForm.reject(Messages.get("error.problem.invalid"));
            }
            return showAddProblemSetProblem(problemSet, problemSetProblemAddForm);
        }

        problemSetProblemService.addProblemSetProblem(problemSet.getJid(), problemJid, problemSetProblemAddData.alias, ProblemSetProblemType.valueOf(problemSetProblemAddData.type), ProblemSetProblemStatus.valueOf(problemSetProblemAddData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        JidCacheServiceImpl.getInstance().putDisplayName(problemJid, SandalphonResourceDisplayNames.getProblemDisplayName(problem), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, problemJid, problem.getSlug()));

        return redirect(routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    public Result removeProblemSetProblem(long problemSetId, long problemSetProblemId) throws ProblemSetNotFoundException, ProblemSetProblemNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemById(problemSetProblemId);

        if (!problemSet.getJid().equals(problemSetProblem.getProblemSetJid())) {
            return notFound();
        }

        problemSetProblemService.removeProblemSetProblem(problemSetProblemId);

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, problemSetProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()))));

        return redirect(routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblemSetProblem(long problemSetId, long problemSetProblemId) throws ProblemSetNotFoundException, ProblemSetProblemNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemById(problemSetProblemId);

        if (!problemSet.getJid().equals(problemSetProblem.getProblemSetJid())) {
            return notFound();
        }

        ProblemSetProblemEditForm problemSetProblemEditData = new ProblemSetProblemEditForm();
        problemSetProblemEditData.alias = problemSetProblem.getAlias();
        problemSetProblemEditData.status = problemSetProblem.getStatus().name();

        Form<ProblemSetProblemEditForm> problemSetProblemEditForm = Form.form(ProblemSetProblemEditForm.class).fill(problemSetProblemEditData);

        return showEditProblemSetProblem(problemSet, problemSetProblem, problemSetProblemEditForm);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postEditProblemSetProblem(long problemSetId, long problemSetProblemId) throws ProblemSetNotFoundException, ProblemSetProblemNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);
        ProblemSetProblem problemSetProblem = problemSetProblemService.findProblemSetProblemById(problemSetProblemId);

        if (!problemSet.getJid().equals(problemSetProblem.getProblemSetJid())) {
            return notFound();
        }

        Form<ProblemSetProblemEditForm> problemSetProblemEditForm = Form.form(ProblemSetProblemEditForm.class).bindFromRequest();
        if (formHasErrors(problemSetProblemEditForm)) {
            return showEditProblemSetProblem(problemSet, problemSetProblem, problemSetProblemEditForm);
        }

        ProblemSetProblemEditForm problemSetProblemEditData = problemSetProblemEditForm.get();
        if (!problemSetProblemEditData.alias.equals(problemSetProblem.getAlias()) && problemSetProblemService.aliasExistsInProblemSet(problemSet.getJid(), problemSetProblemEditData.alias)) {
            problemSetProblemEditForm.reject(Messages.get("error.problemSet.problem.duplicateAlias"));

            return showEditProblemSetProblem(problemSet, problemSetProblem, problemSetProblemEditForm);
        }

        problemSetProblemService.updateProblemSetProblem(problemSetProblem.getId(), problemSetProblemEditData.alias, ProblemSetProblemStatus.valueOf(problemSetProblemEditData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(PROBLEM_SET, problemSet.getJid(), problemSet.getName(), PROBLEM, problemSetProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(problemSetProblem.getProblemJid()))));

        return redirect(routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
    }

    private Result showListProblemSetProblems(ProblemSet problemSet, Page<ProblemSetProblem> pageOfProblemSetProblems, String orderBy, String orderDir, String filterString, Map<String, String> problemSlugsMap) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listProblemSetProblemsView.render(problemSet.getId(), pageOfProblemSetProblems, orderBy, orderDir, filterString, problemSlugsMap));
        template.setSecondaryTitle(Messages.get("archive.problemSet.problems"));
        template.addSecondaryButton(Messages.get("commons.add"), routes.ProblemSetProblemController.addProblemSetProblem(problemSet.getId()));
        if (JerahmeelUtils.hasRole("admin")) {
            appendProblemSubtabs(template, problemSet);
        }
        template.markBreadcrumbLocation(Messages.get("commons.manage"), routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
        template.setPageTitle("Problem Sets - Problems");

        return renderTemplate(template, problemSet);
    }

    private Result showAddProblemSetProblem(ProblemSet problemSet, Form<ProblemSetProblemAddForm> problemSetProblemAddForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addProblemSetProblemView.render(problemSet.getId(), problemSetProblemAddForm));
        if (JerahmeelUtils.hasRole("admin")) {
            appendProblemSubtabs(template, problemSet);
        }
        template.markBreadcrumbLocation(Messages.get("commons.manage"), routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
        template.markBreadcrumbLocation(Messages.get("commons.add"), routes.ProblemSetProblemController.addProblemSetProblem(problemSet.getId()));
        template.setPageTitle("Problem Sets - Problems - Create");

        return renderTemplate(template, problemSet);
    }

    private Result showEditProblemSetProblem(ProblemSet problemSet, ProblemSetProblem problemSetProblem, Form<ProblemSetProblemEditForm> problemSetProblemEditForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editProblemSetProblemView.render(problemSetProblemEditForm, problemSet.getId(), problemSetProblem));
        if (JerahmeelUtils.hasRole("admin")) {
            appendProblemSubtabs(template, problemSet);
        }
        template.markBreadcrumbLocation(Messages.get("commons.manage"), routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
        template.markBreadcrumbLocation(Messages.get("commons.update"), routes.ProblemSetProblemController.editProblemSetProblem(problemSet.getId(), problemSetProblem.getId()));
        template.setPageTitle("Problem Sets - Problems - Edit");

        return renderTemplate(template, problemSet);
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemSet problemSet) {
        appendTabs(template, problemSet);

        template.markBreadcrumbLocation(problemSet.getName(), org.iatoki.judgels.jerahmeel.problemset.routes.ProblemSetController.jumpToProblems(problemSet.getId()));

        return super.renderTemplate(template, problemSet);
    }
}
