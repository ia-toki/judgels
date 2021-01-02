package org.iatoki.judgels.sandalphon.lesson.partner;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.JudgelsPlayUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.jid.JidCacheServiceImpl;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
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
import play.mvc.Result;

@Singleton
public class LessonPartnerController extends AbstractLessonController {

    private static final long PAGE_SIZE = 20;

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
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.LessonPartnerController.addPartner(lesson.getId()));
        template.markBreadcrumbLocation("Partners", routes.LessonPartnerController.viewPartners(lesson.getId()));
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

        Form<LessonPartnerUsernameForm> usernameForm = formFactory.form(LessonPartnerUsernameForm.class);
        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class);

        return showAddPartner(usernameForm, lessonForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = formFactory.form(LessonPartnerUsernameForm.class).bindFromRequest();
        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(usernameForm) || formHasErrors(lessonForm)) {
            return showAddPartner(usernameForm, lessonForm, lesson);
        }

        String username = usernameForm.get().username;
        LessonPartnerUpsertForm lessonData = lessonForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(usernameForm.withError("username", "Username not found"), lessonForm, lesson);
        }

        String userJid = usernameToJidMap.get(username);

        JidCacheServiceImpl.getInstance().putDisplayName(userJid, JudgelsPlayUtils.getUserDisplayName(username), IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)) {
            return showAddPartner(usernameForm.withError("username", "This user is already a partner."), lessonForm, lesson);
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

        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).fill(lessonData);

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

        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).bindFromRequest();

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

        return redirect(routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
    }

    private Result showAddPartner(Form<LessonPartnerUsernameForm> usernameForm, Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(addPartnerView.render(usernameForm, lessonForm, lesson, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.LessonPartnerController.addPartner(lesson.getId()));
        template.setPageTitle("Lesson - Add partner");

        return renderTemplate(template, lessonService, lesson);
    }

    private Result showEditPartner(Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson, LessonPartner lessonPartner) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(lessonForm, lesson, lessonPartner));

        template.setSecondaryTitle("Update partner: " + JidCacheServiceImpl.getInstance().getDisplayName(lessonPartner.getPartnerJid()));
        template.markBreadcrumbLocation("Update partner", routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
        template.setPageTitle("Lesson - Update partner");

        return renderTemplate(template, lessonService, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.markBreadcrumbLocation("Partners", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToPartners(lesson.getId()));

        return super.renderTemplate(template, lessonService, lesson);
    }
}
