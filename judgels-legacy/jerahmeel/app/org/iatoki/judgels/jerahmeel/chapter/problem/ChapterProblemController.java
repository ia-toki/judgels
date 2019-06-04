package org.iatoki.judgels.jerahmeel.chapter.problem;

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
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.AbstractChapterController;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.addChapterProblemView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.editChapterProblemView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.listChapterProblemsView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.viewProblemView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterProblemController extends AbstractChapterController {

    private static final long PAGE_SIZE = 20;
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;
    private final SandalphonClientAPI sandalphonClientAPI;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;

    @Inject
    public ChapterProblemController(@Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader, ClientProblemService clientProblemService, SandalphonClientAPI sandalphonClientAPI, ChapterProblemService chapterProblemService, ChapterService chapterService) {
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
        this.sandalphonClientAPI = sandalphonClientAPI;
        this.chapterProblemService = chapterProblemService;
        this.chapterService = chapterService;
    }

    @Transactional(readOnly = true)
    public Result viewChapterProblems(long chapterId) throws ChapterNotFoundException {
        return listChapterProblems(chapterId, 0, "alias", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listChapterProblems(long chapterId, long page, String orderBy, String orderDir, String filterString) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);

        Page<ChapterProblem> pageOfChapterProblems = chapterProblemService.getPageOfChapterProblems(chapter.getJid(), page, PAGE_SIZE, orderBy, orderDir, filterString);
        List<String> problemJids = pageOfChapterProblems.getData().stream().map(cp -> cp.getProblemJid()).collect(Collectors.toList());
        Map<String, String> problemSlugsMap = SandalphonResourceDisplayNameUtils.buildSlugsMap(JidCacheServiceImpl.getInstance().getDisplayNames(problemJids));

        return showListChapterProblems(chapter, pageOfChapterProblems, orderBy, orderDir, filterString, problemSlugsMap);
    }

    @Transactional(readOnly = true)
    public Result viewChapterProblem(long chapterId, long chapterProblemId) throws ChapterNotFoundException, ChapterProblemNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if (!chapter.getJid().equals(chapterProblem.getChapterJid())) {
            return forbidden();
        }

        String requestUrl;
        String requestBody;

        if (ChapterProblemType.BUNDLE.equals(chapterProblem.getType())) {
            SandalphonBundleProblemStatementRenderRequestParam param = new SandalphonBundleProblemStatementRenderRequestParam();

            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.chapter.submission.bundle.routes.ChapterBundleSubmissionController.postSubmitProblem(chapter.getId(), chapterProblem.getProblemJid()).absoluteURL(request(), request().secure()));

            requestUrl = sandalphonClientAPI.getBundleProblemStatementRenderAPIEndpoint(chapterProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructBundleProblemStatementRenderAPIRequestBody(chapterProblem.getProblemJid(), param);
        } else if (ChapterProblemType.PROGRAMMING.equals(chapterProblem.getType())) {
            SandalphonProgrammingProblemStatementRenderRequestParam param = new SandalphonProgrammingProblemStatementRenderRequestParam();

            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.chapter.submission.programming.routes.ChapterProgrammingSubmissionController.postSubmitProblem(chapter.getId(), chapterProblem.getProblemJid()).absoluteURL(request(), request().secure()));
            param.setReasonNotAllowedToSubmit("");
            param.setAllowedGradingLanguages("");

            requestUrl = sandalphonClientAPI.getProgrammingProblemStatementRenderAPIEndpoint(chapterProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructProgrammingProblemStatementRenderAPIRequestBody(chapterProblem.getProblemJid(), param);
        } else {
            throw new IllegalStateException();
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewProblemView.render(requestUrl, requestBody));
        template.markBreadcrumbLocation(chapterProblem.getAlias(), routes.ChapterProblemController.viewChapterProblem(chapter.getId(), chapterProblem.getId()));

        template.setPageTitle("Chapters - Problem - View");

        return renderTemplate(template, chapter);
    }

    @Transactional(readOnly = true)
    public Result renderImage(long chapterId, long chapterProblemId, String imageFilename) throws ChapterNotFoundException, ChapterProblemNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if (!chapter.getJid().equals(chapterProblem.getChapterJid())) {
            return notFound();
        }

        String imageUrl = sandalphonClientAPI.getProblemStatementMediaRenderAPIEndpoint(chapterProblem.getProblemJid(), imageFilename);

        return redirect(imageUrl);
    }

    public Result switchLanguage() {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        StatementControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addChapterProblem(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterProblemAddForm> chapterProblemAddForm = Form.form(ChapterProblemAddForm.class);

        return showAddChapterProblem(chapter, chapterProblemAddForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddChapterProblem(long chapterId) throws ChapterNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        Form<ChapterProblemAddForm> chapterProblemAddForm = Form.form(ChapterProblemAddForm.class).bindFromRequest();

        if (formHasErrors(chapterProblemAddForm)) {
            return showAddChapterProblem(chapter, chapterProblemAddForm);
        }

        ChapterProblemAddForm chapterProblemCreateData = chapterProblemAddForm.get();

        if (chapterProblemService.aliasExistsInChapter(chapter.getJid(), chapterProblemCreateData.alias)) {
            chapterProblemAddForm.reject(Messages.get("error.chapter.problem.duplicateAlias"));

            return showAddChapterProblem(chapter, chapterProblemAddForm);
        }

        Map<String, String> problemSlugToJidMap = clientProblemService.translateAllowedSlugsToJids(sandalphonClientAuthHeader, IdentityUtils.getUserJid(), ImmutableSet.of(chapterProblemCreateData.problemSlug));
        if (!problemSlugToJidMap.containsKey(chapterProblemCreateData.problemSlug)) {
            chapterProblemAddForm.reject(Messages.get("error.problem.invalid"));
            return showAddChapterProblem(chapter, chapterProblemAddForm);
        }

        String problemJid = problemSlugToJidMap.get(chapterProblemCreateData.problemSlug);
        ProblemInfo problem;
        try {
            problem = clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);
        } catch (RemoteException e) {
            if (e.getStatus() >= Http.Status.INTERNAL_SERVER_ERROR) {
                chapterProblemAddForm.reject(Messages.get("error.system.sandalphon.connection"));
            } else {
                chapterProblemAddForm.reject(Messages.get("error.problem.invalid"));
            }
            return showAddChapterProblem(chapter, chapterProblemAddForm);
        }

        chapterProblemService.addChapterProblem(chapter.getJid(), problemJid, chapterProblemCreateData.alias, ChapterProblemType.valueOf(chapterProblemCreateData.type), ChapterProblemStatus.valueOf(chapterProblemCreateData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        JidCacheServiceImpl.getInstance().putDisplayName(problemJid, SandalphonResourceDisplayNames.getProblemDisplayName(problem), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, problemJid, problem.getSlug()));

        return redirect(routes.ChapterProblemController.viewChapterProblems(chapter.getId()));
    }

    @Transactional
    public Result removeChapterProblem(long chapterId, long chapterProblemId) throws ChapterNotFoundException, ChapterProblemNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if (!chapter.getJid().equals(chapterProblem.getChapterJid())) {
            return forbidden();
        }

        chapterProblemService.removeChapterProblem(chapterProblemId);

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.REMOVE_FROM.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()))));

        return redirect(routes.ChapterProblemController.viewChapterProblems(chapter.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editChapterProblem(long chapterId, long chapterProblemId) throws ChapterNotFoundException, ChapterProblemNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if (!chapter.getJid().equals(chapterProblem.getChapterJid())) {
            return notFound();
        }

        ChapterProblemEditForm chapterProblemEditData = new ChapterProblemEditForm();
        chapterProblemEditData.alias = chapterProblem.getAlias();
        chapterProblemEditData.status = chapterProblem.getStatus().name();

        Form<ChapterProblemEditForm> chapterProblemEditForm = Form.form(ChapterProblemEditForm.class).fill(chapterProblemEditData);

        return showEditChapterProblem(chapter, chapterProblem, chapterProblemEditForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditChapterProblem(long chapterId, long chapterProblemId) throws ChapterNotFoundException, ChapterProblemNotFoundException {
        Chapter chapter = chapterService.findChapterById(chapterId);
        ChapterProblem chapterProblem = chapterProblemService.findChapterProblemById(chapterProblemId);

        if (!chapter.getJid().equals(chapterProblem.getChapterJid())) {
            return notFound();
        }

        Form<ChapterProblemEditForm> chapterProblemEditForm = Form.form(ChapterProblemEditForm.class).bindFromRequest();
        if (formHasErrors(chapterProblemEditForm)) {
            return showEditChapterProblem(chapter, chapterProblem, chapterProblemEditForm);
        }

        ChapterProblemEditForm chapterProblemEditData = chapterProblemEditForm.get();
        if (!chapterProblemEditData.alias.equals(chapterProblem.getAlias()) && chapterProblemService.aliasExistsInChapter(chapter.getJid(), chapterProblemEditData.alias)) {
            chapterProblemEditForm.reject(Messages.get("error.chapter.problem.duplicateAlias"));

            return showEditChapterProblem(chapter, chapterProblem, chapterProblemEditForm);
        }

        chapterProblemService.updateChapterProblem(chapterProblem.getId(), chapterProblemEditData.alias, ChapterProblemStatus.valueOf(chapterProblemEditData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, chapterProblem.getProblemJid(), SandalphonResourceDisplayNameUtils.parseSlugByLanguage(JidCacheServiceImpl.getInstance().getDisplayName(chapterProblem.getProblemJid()))));

        return redirect(routes.ChapterProblemController.viewChapterProblems(chapter.getId()));
    }

    private Result showListChapterProblems(Chapter chapter, Page<ChapterProblem> pageOfChapterProblems, String orderBy, String orderDir, String filterString, Map<String, String> problemSlugsMap) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listChapterProblemsView.render(chapter.getId(), pageOfChapterProblems, orderBy, orderDir, filterString, problemSlugsMap));
        template.setSecondaryTitle(Messages.get("chapter.problems"));
        template.addSecondaryButton(Messages.get("commons.add"), routes.ChapterProblemController.addChapterProblem(chapter.getId()));
        template.setPageTitle("Chapters - Problems");

        return renderTemplate(template, chapter);
    }

    private Result showAddChapterProblem(Chapter chapter, Form<ChapterProblemAddForm> chapterProblemAddForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addChapterProblemView.render(chapter.getId(), chapterProblemAddForm));
        template.markBreadcrumbLocation(Messages.get("commons.add"), routes.ChapterProblemController.addChapterProblem(chapter.getId()));
        template.setPageTitle("Chapters - Problems - Create");

        return renderTemplate(template, chapter);
    }

    private Result showEditChapterProblem(Chapter chapter, ChapterProblem chapterProblem, Form<ChapterProblemEditForm> chapterProblemEditForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editChapterProblemView.render(chapterProblemEditForm, chapter.getId(), chapterProblem));
        template.markBreadcrumbLocation(Messages.get("commons.update"), routes.ChapterProblemController.editChapterProblem(chapter.getId(), chapterProblem.getId()));
        template.setPageTitle("Chapters - Problems - Edit");

        return renderTemplate(template, chapter);
    }

    private Result renderTemplate(HtmlTemplate template, Chapter chapter) {
        appendTabs(template, chapter);

        template.markBreadcrumbLocation(Messages.get("chapter.problems"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToProblems(chapter.getId()));

        return super.renderTemplate(template);
    }
}
