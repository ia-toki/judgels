package org.iatoki.judgels.sandalphon.lesson.partner;

import com.google.common.collect.ImmutableSet;
import judgels.jophiel.api.user.search.UserSearchService;
import org.iatoki.judgels.jophiel.JophielClientControllerUtils;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.*;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonNotFoundException;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.lesson.partner.html.addPartnerView;
import org.iatoki.judgels.sandalphon.lesson.partner.html.editPartnerView;
import org.iatoki.judgels.sandalphon.lesson.partner.html.listPartnersView;
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
public class LessonPartnerController extends AbstractLessonController {

    private static final long PAGE_SIZE = 20;
    private static final String LESSON = "lesson";
    private static final String PARTNER = "partner";

    private final UserSearchService userSearchService;
    private final LessonService lessonService;

    @Inject
    public LessonPartnerController(UserSearchService userSearchService, LessonService lessonService) {
        this.userSearchService = userSearchService;
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

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listPartnersView.render(lesson.getId(), pageOfLessonPartners, orderBy, orderDir));
        template.setSecondaryTitle(Messages.get("lesson.partner.list"));
        template.addSecondaryButton(Messages.get("lesson.partner.add"), routes.LessonPartnerController.addPartner(lesson.getId()));
        template.markBreadcrumbLocation(Messages.get("lesson.partner.list"), routes.LessonPartnerController.viewPartners(lesson.getId()));
        template.setPageTitle("Lesson - Partners");

        return renderTemplate(template, lessonService, lesson);
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

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            usernameForm.reject("username", Messages.get("lesson.partner.usernameNotFound"));
            return showAddPartner(usernameForm, lessonForm, lesson);
        }

        String userJid = usernameToJidMap.get(username);

        JidCacheServiceImpl.getInstance().putDisplayName(userJid, JudgelsPlayUtils.getUserDisplayName(username), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)) {
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

        lessonService.createLessonPartner(lesson.getJid(), userJid, partnerConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(LESSON, lesson.getJid(), lesson.getSlug(), PARTNER, userJid, username));

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

    private Result showAddPartner(Form<LessonPartnerUsernameForm> usernameForm, Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addPartnerView.render(usernameForm, lessonForm, lesson, JophielClientControllerUtils.getInstance().getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle(Messages.get("lesson.partner.add"));
        template.markBreadcrumbLocation(Messages.get("lesson.partner.add"), routes.LessonPartnerController.addPartner(lesson.getId()));
        template.setPageTitle("Lesson - Add Partner");

        return renderTemplate(template, lessonService, lesson);
    }

    private Result showEditPartner(Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson, LessonPartner lessonPartner) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(lessonForm, lesson, lessonPartner));

        template.setSecondaryTitle(Messages.get("lesson.partner.update") + ": " + JidCacheServiceImpl.getInstance().getDisplayName(lessonPartner.getPartnerJid()));
        template.markBreadcrumbLocation(Messages.get("lesson.partner.update"), routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
        template.setPageTitle("Lesson - Update Partner");

        return renderTemplate(template, lessonService, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.markBreadcrumbLocation(Messages.get("lesson.partner"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToPartners(lesson.getId()));

        return super.renderTemplate(template, lessonService, lesson);
    }
}
