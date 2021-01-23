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
import org.iatoki.judgels.play.actor.ActorChecker;
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
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public class LessonPartnerController extends AbstractLessonController {

    private static final long PAGE_SIZE = 20;

    private final ActorChecker actorChecker;
    private final UserSearchService userSearchService;
    private final LessonService lessonService;
    private final ProfileService profileService;

    @Inject
    public LessonPartnerController(
            ActorChecker actorChecker,
            UserSearchService userSearchService,
            LessonService lessonService,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.userSearchService = userSearchService;
        this.lessonService = lessonService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result viewPartners(Http.Request req, long lessonId) {
        return listPartners(req, lessonId, 0, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Result listPartners(Http.Request req, long lessonId, long pageIndex, String orderBy, String orderDir) {
        actorChecker.check(req);

        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Page<LessonPartner> pageOfLessonPartners = lessonService.getPageOfLessonPartners(lesson.getJid(), pageIndex, PAGE_SIZE, orderBy, orderDir);

        Set<String> userJids = pageOfLessonPartners.getPage().stream().map(LessonPartner::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listPartnersView.render(lesson.getId(), pageOfLessonPartners, profilesMap, orderBy, orderDir));
        template.setSecondaryTitle("Partners");
        template.addSecondaryButton("Add partner", routes.LessonPartnerController.addPartner(lesson.getId()));
        template.markBreadcrumbLocation("Partners", routes.LessonPartnerController.viewPartners(lesson.getId()));
        template.setPageTitle("Lesson - Partners");

        return renderTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result addPartner(Http.Request req, long lessonId) {
        actorChecker.check(req);

        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = formFactory.form(LessonPartnerUsernameForm.class);
        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class);

        return showAddPartner(req, usernameForm, lessonForm, lesson);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postAddPartner(Http.Request req, long lessonId) {
        actorChecker.check(req);

        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        Form<LessonPartnerUsernameForm> usernameForm = formFactory.form(LessonPartnerUsernameForm.class).bindFromRequest(req);
        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(usernameForm) || formHasErrors(lessonForm)) {
            return showAddPartner(req, usernameForm, lessonForm, lesson);
        }

        String username = usernameForm.get().username;
        LessonPartnerUpsertForm lessonData = lessonForm.get();

        Map<String, String> usernameToJidMap = userSearchService.translateUsernamesToJids(ImmutableSet.of(username));

        if (!usernameToJidMap.containsKey(username)) {
            return showAddPartner(req, usernameForm.withError("username", "Username not found"), lessonForm, lesson);
        }

        String userJid = usernameToJidMap.get(username);

        if (lessonService.isUserPartnerForLesson(lesson.getJid(), userJid)) {
            return showAddPartner(req, usernameForm.withError("username", "This user is already a partner."), lessonForm, lesson);
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

        lessonService.createLessonPartner(lesson.getJid(), userJid, partnerConfig);

        return redirect(routes.LessonPartnerController.viewPartners(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editPartner(Http.Request req, long lessonId, long partnerId) {
        actorChecker.check(req);

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

        return showEditPartner(req, lessonForm, lesson, lessonPartner);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditPartner(Http.Request req, long lessonId, long partnerId) {
        actorChecker.check(req);

        Lesson lesson = checkFound(lessonService.findLessonById(lessonId));

        if (!LessonControllerUtils.isAuthorOrAbove(lesson)) {
            return notFound();
        }

        LessonPartner lessonPartner = checkFound(lessonService.findLessonPartnerById(partnerId));

        Form<LessonPartnerUpsertForm> lessonForm = formFactory.form(LessonPartnerUpsertForm.class).bindFromRequest(req);

        if (formHasErrors(lessonForm)) {
            return showEditPartner(req, lessonForm, lesson, lessonPartner);
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

        lessonService.updateLessonPartner(partnerId, lessonConfig);

        return redirect(routes.LessonPartnerController.editPartner(lesson.getId(), lessonPartner.getId()));
    }

    private Result showAddPartner(Http.Request req, Form<LessonPartnerUsernameForm> usernameForm, Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(addPartnerView.render(usernameForm, lessonForm, lesson, getUserAutocompleteAPIEndpoint()));

        template.setSecondaryTitle("Add partner");
        template.markBreadcrumbLocation("Add partner", routes.LessonPartnerController.addPartner(lesson.getId()));
        template.setPageTitle("Lesson - Add partner");

        return renderTemplate(template, lessonService, lesson);
    }

    private Result showEditPartner(Http.Request req, Form<LessonPartnerUpsertForm> lessonForm, Lesson lesson, LessonPartner lessonPartner) {
        Profile profile = profileService.getProfile(lessonPartner.getUserJid());

        HtmlTemplate template = getBaseHtmlTemplate(req);
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
