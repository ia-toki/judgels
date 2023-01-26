package org.iatoki.judgels.sandalphon.lesson.version;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.sandalphon.GitCommit;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.LessonStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonRoleChecker;
import org.iatoki.judgels.sandalphon.lesson.version.html.listVersionsView;
import org.iatoki.judgels.sandalphon.lesson.version.html.viewVersionLocalChangesView;
import org.iatoki.judgels.sandalphon.resource.VersionCommitForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class LessonVersionController extends AbstractLessonController {
    private final LessonStore lessonStore;
    private final LessonRoleChecker lessonRoleChecker;
    private final ProfileService profileService;

    @Inject
    public LessonVersionController(
            LessonStore lessonStore,
            LessonRoleChecker lessonRoleChecker,
            ProfileService profileService) {

        super(lessonStore, lessonRoleChecker);
        this.lessonStore = lessonStore;
        this.lessonRoleChecker = lessonRoleChecker;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result listVersionHistory(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isAllowedToViewVersionHistory(req, lesson));

        List<GitCommit> versions = lessonStore.getVersions(actorJid, lesson.getJid());

        Set<String> userJids = versions.stream().map(GitCommit::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        boolean isClean = !lessonStore.userCloneExists(actorJid, lesson.getJid());
        boolean isAllowedToRestoreVersionHistory = isClean && lessonRoleChecker.isAllowedToRestoreVersionHistory(req, lesson);

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(listVersionsView.render(versions, lesson.getId(), profilesMap, isAllowedToRestoreVersionHistory));
        template.markBreadcrumbLocation("History", routes.LessonVersionController.listVersionHistory(lesson.getId()));
        template.setPageTitle("Lesson - Versions - History");

        return renderTemplate(template, lesson);
    }

    @Transactional(readOnly = true)
    public Result restoreVersionHistory(Http.Request req, long lessonId, String hash) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        boolean isClean = !lessonStore.userCloneExists(actorJid, lesson.getJid());
        checkAllowed(lessonRoleChecker.isAllowedToRestoreVersionHistory(req, lesson) && isClean);

        lessonStore.restore(lesson.getJid(), hash);

        return redirect(routes.LessonVersionController.listVersionHistory(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        boolean isClean = !lessonStore.userCloneExists(actorJid, lesson.getJid());

        Form<VersionCommitForm> form = formFactory.form(VersionCommitForm.class);

        return showViewVersionLocalChanges(req, form, lesson, isClean);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCommitVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        Form<VersionCommitForm> form = formFactory.form(VersionCommitForm.class).bindFromRequest(req);
        if (formHasErrors(form)) {
            boolean isClean = !lessonStore.userCloneExists(actorJid, lesson.getJid());
            return showViewVersionLocalChanges(req, form, lesson, isClean);
        }

        VersionCommitForm data = form.get();
        String localChangesErrorFlash = "";

        if (lessonStore.fetchUserClone(actorJid, lesson.getJid())) {
            localChangesErrorFlash = "Your working copy has diverged from the master copy. Please update your working copy.";
        } else if (!lessonStore.commitThenMergeUserClone(actorJid, lesson.getJid(), data.title, data.description)) {
            localChangesErrorFlash = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else if (!lessonStore.pushUserClone(actorJid, lesson.getJid())) {
            localChangesErrorFlash = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        } else {
            lessonStore.discardUserClone(actorJid, lesson.getJid());
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()))
                .flashing("localChangesError", localChangesErrorFlash);
    }

    @Transactional(readOnly = true)
    public Result editVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        lessonStore.fetchUserClone(actorJid, lesson.getJid());

        String localChangesErrorFlash = "";
        if (!lessonStore.updateUserClone(actorJid, lesson.getJid())) {
            localChangesErrorFlash = "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.";
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()))
                .flashing("localChangesError", localChangesErrorFlash);
    }

    @Transactional(readOnly = true)
    public Result discardVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        lessonStore.discardUserClone(actorJid, lesson.getJid());

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
    }

    private Result showViewVersionLocalChanges(Http.Request req, Form<VersionCommitForm> form, Lesson lesson, boolean isClean) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewVersionLocalChangesView.render(form, lesson, isClean, req.flash().getOptional("localChangesError").orElse(null)));
        template.markBreadcrumbLocation("Local changes", routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
        template.setPageTitle("Lesson - Versions - Local changes");

        return renderTemplate(template, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, Lesson lesson) {
        template.addSecondaryTab("Local changes", routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));

        if (lessonRoleChecker.isAllowedToViewVersionHistory(template.getRequest(), lesson)) {
            template.addSecondaryTab("History", routes.LessonVersionController.listVersionHistory(lesson.getId()));
        }

        template.markBreadcrumbLocation("Versions", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToVersions(lesson.getId()));

        return super.renderTemplate(template, lesson);
    }
}
