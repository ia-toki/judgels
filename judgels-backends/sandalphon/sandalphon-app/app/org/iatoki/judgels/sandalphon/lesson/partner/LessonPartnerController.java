package org.iatoki.judgels.sandalphon.lesson.partner;

import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.jophiel.api.user.search.UserSearchService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.partner.LessonPartner;
import judgels.sandalphon.api.lesson.partner.LessonPartnerConfig;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
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
    private final ProfileService profileService;

    @Inject
    public LessonPartnerController(UserSearchService userSearchService, LessonService lessonService, ProfileService profileService) {
        this.userSearchService = userSearchService;
        this.lessonService = lessonService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(long lessonId) {
        return listPartners(lessonId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(long lessonId, long pageIndex, String orderBy, String orderDir) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Page<LessonPartner> pageOfLessonPartners = lessonService.getPageOfLessonPartners(lesson.getJid(), pageIndex, PAGE_SIZE, orderBy, orderDir);

        Set<String> userJids = pageOfLessonPartners.getPage().stream().map(LessonPartner::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listPartnersView.render(lesson.getId(), pageOfLessonPartners, profilesMap, orderBy, orderDir));
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.LessonPartnerController.addPartner(lesson.getId()));
        template.markBreadcrumbLocation("Partners", routes.LessonPartnerController.viewPartners(lesson.getId()));
        template.setPageTitle("Lesson - Partners");

        return renderTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(long lessonId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = formFactory.form(LessonPartnerUsernameForm.class);
        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class);

        return showAddPartner(usernameForm, lessonForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(long lessonId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

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

        if (lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)) {
            return showAddPartner(usernameForm.withError("username", "This user is already a partner."), lessonForm, lesson);
        }

        LessonPartnerConfig partnerConfig = new LessonPartnerConfig.Builder()
                .isAllowedToUpdateLesson(lessonData.isAllowedToUpdateLesson)
                .isAllowedToUpdateStatement(lessonData.isAllowedToUpdateStatement)
                .isAllowedToUploadStatementResources(lessonData.isAllowedToUploadStatementResources)
                .allowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToView))
                .allowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToUpdate))
                .isAllowedToManageStatementLanguages(lessonData.isAllowedToManageStatementLanguages)
                .isAllowedToViewVersionHistory(lessonData.isAllowedToViewVersionHistory)
                .isAllowedToRestoreVersionHistory(lessonData.isAllowedToRestoreVersionHistory)
                .build();

        lessonService.createLessonPartner(lesson.getJid(), userJid, partnerConfig, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.LessonPartnerController.viewPartners(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(long lessonId, long partnerId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        LessonPartner lessonPartner = checkFound(lessonService.findLessonPartnerById(partnerId));

        LessonPartnerConfig lessonConfig = lessonPartner.getConfig();
        LessonPartnerUpsertForm lessonData = new LessonPartnerUpsertForm();

        lessonData.isAllowedToUpdateLesson = lessonConfig.getIsAllowedToUpdateLesson();
        lessonData.isAllowedToUpdateStatement = lessonConfig.getIsAllowedToUpdateStatement();
        lessonData.isAllowedToUploadStatementResources = lessonConfig.getIsAllowedToUploadStatementResources();
        lessonData.allowedStatementLanguagesToView = PartnerControllerUtils.combineByComma(lessonConfig.getAllowedStatementLanguagesToView());
        lessonData.allowedStatementLanguagesToUpdate = PartnerControllerUtils.combineByComma(lessonConfig.getAllowedStatementLanguagesToUpdate());
        lessonData.isAllowedToManageStatementLanguages = lessonConfig.getIsAllowedToManageStatementLanguages();
        lessonData.isAllowedToViewVersionHistory = lessonConfig.getIsAllowedToViewVersionHistory();
        lessonData.isAllowedToRestoreVersionHistory = lessonConfig.getIsAllowedToRestoreVersionHistory();

        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).fill(lessonData);

        return showEditPartner(lessonForm, lesson, lessonPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(long lessonId, long partnerId) {
        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        LessonPartner lessonPartner = checkFound(lessonService.findLessonPartnerById(partnerId));

        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).bindFromRequest();

        if (formHasErrors(lessonForm)) {
            return showEditPartner(lessonForm, lesson, lessonPartner);
        }

        LessonPartnerUpsertForm lessonData = lessonForm.get();

        LessonPartnerConfig lessonConfig = new LessonPartnerConfig.Builder()
                .isAllowedToUpdateLesson(lessonData.isAllowedToUpdateLesson)
                .isAllowedToUpdateStatement(lessonData.isAllowedToUpdateStatement)
                .isAllowedToUploadStatementResources(lessonData.isAllowedToUploadStatementResources)
                .allowedStatementLanguagesToView(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToView))
                .allowedStatementLanguagesToUpdate(PartnerControllerUtils.splitByComma(lessonData.allowedStatementLanguagesToUpdate))
                .isAllowedToManageStatementLanguages(lessonData.isAllowedToManageStatementLanguages)
                .isAllowedToViewVersionHistory(lessonData.isAllowedToViewVersionHistory)
                .isAllowedToRestoreVersionHistory(lessonData.isAllowedToRestoreVersionHistory)
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
        Profile profile = profileService.getProfile(lessonPartner.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editPartnerView.render(lessonForm, lesson, lessonPartner));

        template.setSecondaryTitle("Update partner: " + profile.getUsername());
        template.markBreadcrumbLocation("Update partner", routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
        template.setPageTitle("Lesson - Update partner");

        return renderTemplate(template, lessonService, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.markBreadcrumbLocation("Partners", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToPartners(lesson.getId()));

        return super.renderTemplate(template, lessonService, lesson);
    }
}
