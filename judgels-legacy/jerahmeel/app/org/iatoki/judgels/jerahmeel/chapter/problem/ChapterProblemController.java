package org.iatoki.judgels.jerahmeel.chapter.problem;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.api.JudgelsAPIClientException;
import org.iatoki.judgels.api.sandalphon.SandalphonBundleProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonClientAPI;
import org.iatoki.judgels.api.sandalphon.SandalphonProblem;
import org.iatoki.judgels.api.sandalphon.SandalphonProgrammingProblemStatementRenderRequestParam;
import org.iatoki.judgels.api.sandalphon.SandalphonResourceDisplayNameUtils;
import org.iatoki.judgels.jerahmeel.chapter.Chapter;
import org.iatoki.judgels.jerahmeel.chapter.ChapterNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.chapter.ChapterControllerUtils;
import org.iatoki.judgels.jerahmeel.StatementControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.chapter.ChapterService;
import org.iatoki.judgels.jerahmeel.jid.JidCacheServiceImpl;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.addChapterProblemView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.editChapterProblemView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.listChapterProblemsView;
import org.iatoki.judgels.jerahmeel.chapter.problem.html.viewProblemView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ChapterProblemController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String PROBLEM = "problem";
    private static final String CHAPTER = "chapter";

    private final SandalphonClientAPI sandalphonClientAPI;
    private final ChapterProblemService chapterProblemService;
    private final ChapterService chapterService;

    @Inject
    public ChapterProblemController(SandalphonClientAPI sandalphonClientAPI, ChapterProblemService chapterProblemService, ChapterService chapterService) {
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

            param.setProblemSecret(chapterProblem.getProblemSecret());
            param.setCurrentMillis(System.currentTimeMillis());
            param.setStatementLanguage(StatementControllerUtils.getCurrentStatementLanguage());
            param.setSwitchStatementLanguageUrl(org.iatoki.judgels.jerahmeel.training.course.chapter.problem.routes.TrainingProblemController.switchLanguage().absoluteURL(request(), request().secure()));
            param.setPostSubmitUrl(org.iatoki.judgels.jerahmeel.chapter.submission.bundle.routes.ChapterBundleSubmissionController.postSubmitProblem(chapter.getId(), chapterProblem.getProblemJid()).absoluteURL(request(), request().secure()));

            requestUrl = sandalphonClientAPI.getBundleProblemStatementRenderAPIEndpoint(chapterProblem.getProblemJid());
            requestBody = sandalphonClientAPI.constructBundleProblemStatementRenderAPIRequestBody(chapterProblem.getProblemJid(), param);
        } else if (ChapterProblemType.PROGRAMMING.equals(chapterProblem.getType())) {
            SandalphonProgrammingProblemStatementRenderRequestParam param = new SandalphonProgrammingProblemStatementRenderRequestParam();

            param.setProblemSecret(chapterProblem.getProblemSecret());
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

        LazyHtml content = new LazyHtml(viewProblemView.render(requestUrl, requestBody));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
                new InternalLink(chapterProblem.getAlias(), routes.ChapterProblemController.viewChapterProblem(chapter.getId(), chapterProblem.getId()))
        );

        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Problem - View");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
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

        SandalphonProblem sandalphonProblem;
        try {
            sandalphonProblem = sandalphonClientAPI.findClientProblem(chapterProblemCreateData.problemJid, chapterProblemCreateData.problemSecret);
        } catch (JudgelsAPIClientException e) {
            if (e.getStatusCode() >= Http.Status.INTERNAL_SERVER_ERROR) {
                chapterProblemAddForm.reject(Messages.get("error.system.sandalphon.connection"));
            } else {
                chapterProblemAddForm.reject(Messages.get("error.problem.invalid"));
            }
            return showAddChapterProblem(chapter, chapterProblemAddForm);
        }

        chapterProblemService.addChapterProblem(chapter.getJid(), chapterProblemCreateData.problemJid, chapterProblemCreateData.problemSecret, chapterProblemCreateData.alias, ChapterProblemType.valueOf(chapterProblemCreateData.type), ChapterProblemStatus.valueOf(chapterProblemCreateData.status), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
        JidCacheServiceImpl.getInstance().putDisplayName(chapterProblemCreateData.problemJid, sandalphonProblem.getDisplayName(), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(CHAPTER, chapter.getJid(), chapter.getName(), PROBLEM, sandalphonProblem.getJid(), sandalphonProblem.getSlug()));

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
        LazyHtml content = new LazyHtml(listChapterProblemsView.render(chapter.getId(), pageOfChapterProblems, orderBy, orderDir, filterString, problemSlugsMap));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("chapter.problems"), new InternalLink(Messages.get("commons.add"), routes.ChapterProblemController.addChapterProblem(chapter.getId())), c));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Problems");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showAddChapterProblem(Chapter chapter, Form<ChapterProblemAddForm> chapterProblemAddForm) {
        LazyHtml content = new LazyHtml(addChapterProblemView.render(chapter.getId(), chapterProblemAddForm));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
                new InternalLink(Messages.get("commons.add"), routes.ChapterProblemController.addChapterProblem(chapter.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Problems - Create");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditChapterProblem(Chapter chapter, ChapterProblem chapterProblem, Form<ChapterProblemEditForm> chapterProblemEditForm) {
        LazyHtml content = new LazyHtml(editChapterProblemView.render(chapterProblemEditForm, chapter.getId(), chapterProblem));
        ChapterControllerUtils.appendTabLayout(content, chapter);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, chapter,
                new InternalLink(Messages.get("commons.update"), routes.ChapterProblemController.editChapterProblem(chapter.getId(), chapterProblem.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Chapters - Problems - Edit");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Chapter chapter, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = ChapterControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(new InternalLink(Messages.get("chapter.problems"), org.iatoki.judgels.jerahmeel.chapter.routes.ChapterController.jumpToProblems(chapter.getId())));
        breadcrumbsBuilder.add(new InternalLink(Messages.get("commons.view"), routes.ChapterProblemController.viewChapterProblems(chapter.getId())));
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
