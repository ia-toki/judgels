package org.iatoki.judgels.sandalphon.lesson.partner;

import org.iatoki.judgels.api.jophiel.JophielPublicAPI;
import org.iatoki.judgels.api.jophiel.JophielUser;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.heading3Layout;
import org.iatoki.judgels.play.views.html.layouts.heading3WithActionLayout;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.lesson.LessonNotFoundException;
import org.iatoki.judgels.sandalphon.resource.PartnerControllerUtils;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.lesson.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.lesson.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.lesson.partner.html.listPartnersView;
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
public class LessonPartnerController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String LESSON = "lesson";
    private static final String PARTNER = "partner";

    private final JophielPublicAPI jophielPublicAPI;
    private final LessonService lessonService;

    @Inject
    public LessonPartnerController(JophielPublicAPI jophielPublicAPI, LessonService lessonService) {
        this.jophielPublicAPI = jophielPublicAPI;
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(long lessonId) throws LessonNotFoundException {
        return listPartners(lessonId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(long lessonId, long pageIndex, String orderBy, String orderDir) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Page<LessonPartner> pageOfLessonPartners = lessonService.getPageOfLessonPartners(lesson.getJid(), pageIndex, PAGE_SIZE, orderBy, orderDir);

        LazyHtml content = new LazyHtml(listPartnersView.render(lesson.getId(), pageOfLessonPartners, orderBy, orderDir));
        content.appendLayout(c -> heading3WithActionLayout.render(Messages.get("lesson.partner.list"), new InternalLink(Messages.get("lesson.partner.add"), routes.LessonPartnerController.addPartner(lesson.getId())), c));
        LessonControllerUtils.appendTabsLayout(content, lessonService, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        LessonControllerUtils.appendTitleLayout(content, lessonService, lesson);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, lesson, new InternalLink(Messages.get("lesson.partner.list"), routes.LessonPartnerController.viewPartners(lesson.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Partners");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = Form.form(LessonPartnerUsernameForm.class);
        Form<LessonPartnerUpsertForm> lessonForm = Form.form(LessonPartnerUpsertForm.class);

        return showAddPartner(usernameForm, lessonForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = Form.form(LessonPartnerUsernameForm.class).bindFromRequest();
        Form<LessonPartnerUpsertForm> lessonForm = Form.form(LessonPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(lessonForm)) {
            return showAddPartner(usernameForm, lessonForm, lesson);
        }

        String username = usernameForm.get().username;
        LessonPartnerUpsertForm lessonData = lessonForm.get();

        JophielUser jophielUser = jophielPublicAPI.findUserByUsername(username);

        if (jophielUser == null) {
            usernameForm.reject("username", Messages.get("lesson.partner.usernameNotFound"));
            return showAddPartner(usernameForm, lessonForm, lesson);
        }

        JidCacheServiceImpl.getInstance().putDisplayName(jophielUser.getJid(), JudgelsPlayUtils.getUserDisplayName(jophielUser.getUsername()), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (lessonService.isUserPartnerForLesson(lesson.getJid(), jophielUser.getJid())) {
            usernameForm.reject("username", Messages.get("lesson.partner.already"));
            return showAddPartner(usernameForm, lessonForm, lesson);
        }

        LessonPartnerConfig partnerConfig = new LessonPartnerConfigBuilder()
              .setIsAllowedToUpdateLesson(lessonData.isAllowedToUpdateLesson)
              .setIsAllowedToUpdateStatement(lessonData.isAllowedToUpdateStatement)
              .setIsAllowedToUploadStatementResources(lessonData.isAllowedToUploadStatementResources)
              .setAllowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToView))
              .setAllowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToUpdate))
              .setIsAllowedToManageStatementLanguages(lessonData.isAllowedToManageStatementLanguages)
              .setIsAllowedToViewVersionHistory(lessonData.isAllowedToViewVersionHistory)
              .setIsAllowedToRestoreVersionHistory(lessonData.isAllowedToRestoreVersionHistory)
              .setIsAllowedToManageLessonClients(lessonData.isAllowedToManageLessonClients)
              .build();

        lessonService.createLessonPartner(lesson.getJid(), jophielUser.getJid(), partnerConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(LESSON, lesson.getJid(), lesson.getSlug(), PARTNER, jophielUser.getJid(), jophielUser.getUsername()));

        return redirect(routes.LessonPartnerController.viewPartners(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(long lessonId, long partnerId) throws LessonNotFoundException, LessonPartnerNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        LessonPartner lessonPartner = lessonService.findLessonPartnerById(partnerId);

        LessonPartnerConfig lessonConfig = lessonPartner.getConfig();
        LessonPartnerUpsertForm lessonData = new LessonPartnerUpsertForm();

        lessonData.isAllowedToUpdateLesson = lessonConfig.isAllowedToUpdateLesson();
        lessonData.isAllowedToUpdateStatement = lessonConfig.isAllowedToUpdateStatement();
        lessonData.isAllowedToUploadStatementResources = lessonConfig.isAllowedToUploadStatementResources();
        lessonData.allowedStatementLanguagesToView = PartnerControllerUtils.combineByComma(lessonConfig.getAllowedStatementLanguagesToView());
        lessonData.allowedStatementLanguagesToUpdate = PartnerControllerUtils.combineByComma(lessonConfig.getAllowedStatementLanguagesToUpdate());
        lessonData.isAllowedToManageStatementLanguages = lessonConfig.isAllowedToManageStatementLanguages();
        lessonData.isAllowedToViewVersionHistory = lessonConfig.isAllowedToViewVersionHistory();
        lessonData.isAllowedToRestoreVersionHistory = lessonConfig.isAllowedToRestoreVersionHistory();
        lessonData.isAllowedToManageLessonClients = lessonConfig.isAllowedToManageLessonClients();

        Form<LessonPartnerUpsertForm> lessonForm = Form.form(LessonPartnerUpsertForm.class).fill(lessonData);

        return showEditPartner(lessonForm, lesson, lessonPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(long lessonId, long partnerId) throws LessonNotFoundException, LessonPartnerNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        LessonPartner lessonPartner = lessonService.findLessonPartnerById(partnerId);

        Form<LessonPartnerUpsertForm> lessonForm = Form.form(LessonPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(lessonForm)) {
            return showEditPartner(lessonForm, lesson, lessonPartner);
        }

        LessonPartnerUpsertForm lessonData = lessonForm.get();

        LessonPartnerConfig lessonConfig = new LessonPartnerConfigBuilder()
              .setIsAllowedToUpdateLesson(lessonData.isAllowedToUpdateLesson)
              .setIsAllowedToUpdateStatement(lessonData.isAllowedToUpdateStatement)
              .setIsAllowedToUploadStatementResources(lessonData.isAllowedToUploadStatementResources)
              .setAllowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToView))
              .setAllowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToUpdate))
              .setIsAllowedToManageStatementLanguages(lessonData.isAllowedToManageStatementLanguages)
              .setIsAllowedToViewVersionHistory(lessonData.isAllowedToViewVersionHistory)
              .setIsAllowedToRestoreVersionHistory(lessonData.isAllowedToRestoreVersionHistory)
              .setIsAllowedToManageLessonClients(lessonData.isAllowedToManageLessonClients)
              .build();

        lessonService.updateLessonPartner(partnerId, lessonConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT_IN.construct(LESSON, lesson.getJid(), lesson.getSlug(), PARTNER, lessonPartner.getPartnerJid(), JidCacheServiceImpl.getInstance().getDisplayName(lessonPartner.getPartnerJid())));

        return redirect(routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Lesson lesson, InternalLink lastLink) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                LessonControllerUtils.getLessonBreadcrumbsBuilder(lesson)
                        .add(new InternalLink(Messages.get("lesson.partner"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToPartners(lesson.getId())))
                        .add(lastLink)
                        .build()
        );
    }

    private Result showAddPartner(Form<LessonPartnerUsernameForm> usernameForm, Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson) {
        LazyHtml content = new LazyHtml(addPartnerView.render(usernameForm, lessonForm, lesson, jophielPublicAPI.getUserAutocompleteAPIEndpoint()));

        content.appendLayout(c -> heading3Layout.render(Messages.get("lesson.partner.add"), c));
        LessonControllerUtils.appendTabsLayout(content, lessonService, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        LessonControllerUtils.appendTitleLayout(content, lessonService, lesson);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, lesson, new InternalLink(Messages.get("lesson.partner.add"), routes.LessonPartnerController.addPartner(lesson.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Add Partner");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditPartner(Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson, LessonPartner lessonPartner) {
        LazyHtml content = new LazyHtml(editPartnerView.render(lessonForm, lesson, lessonPartner));

        content.appendLayout(c -> heading3Layout.render(Messages.get("lesson.partner.update") + ": " + JidCacheServiceImpl.getInstance().getDisplayName(lessonPartner.getPartnerJid()), c));
        LessonControllerUtils.appendTabsLayout(content, lessonService, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        LessonControllerUtils.appendTitleLayout(content, lessonService, lesson);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, lesson, new InternalLink(Messages.get("lesson.partner.update"), routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Update Partner");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }
}
