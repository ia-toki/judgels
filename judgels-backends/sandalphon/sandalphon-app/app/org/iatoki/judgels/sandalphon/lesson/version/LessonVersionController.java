package org.iatoki.judgels.sandalphon.lesson.version;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonRoleChecker;
import org.iatoki.judgels.sandalphon.lesson.LessonStore;
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

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class);

        return showViewVersionLocalChanges(req, versionCommitForm, lesson, isClean);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCommitVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class).bindFromRequest(req);
        if (formHasErrors(versionCommitForm)) {
            boolean isClean = !lessonStore.userCloneExists(actorJid, lesson.getJid());
            return showViewVersionLocalChanges(req, versionCommitForm, lesson, isClean);
        }

        VersionCommitForm versionCommitData = versionCommitForm.get();

        if (lessonStore.fetchUserClone(actorJid, lesson.getJid())) {
            flash("localChangesError", "Your working copy has diverged from the master copy. Please update your working copy.");
        } else if (!lessonStore.commitThenMergeUserClone(actorJid, lesson.getJid(), versionCommitData.title, versionCommitData.description)) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else if (!lessonStore.pushUserClone(actorJid, lesson.getJid())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else {
            try {
                lessonStore.discardUserClone(actorJid, lesson.getJid());
            } catch (IOException e) {
                // do nothing
            }
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
    }

    @Transactional(readOnly = true)
    public Result editVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        lessonStore.fetchUserClone(actorJid, lesson.getJid());

        if (!lessonStore.updateUserClone(actorJid, lesson.getJid())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
    }

    @Transactional(readOnly = true)
    public Result discardVersionLocalChanges(Http.Request req, long lessonId) {
        String actorJid = getUserJid(req);
        Lesson lesson = checkFound(lessonStore.findLessonById(lessonId));
        checkAllowed(lessonRoleChecker.isPartnerOrAbove(req, lesson));

        try {
            lessonStore.discardUserClone(actorJid, lesson.getJid());

            return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
        } catch (IOException e) {
            return notFound();
        }
    }

    private Result showViewVersionLocalChanges(Http.Request req, Form<VersionCommitForm> versionCommitForm, Lesson lesson, boolean isClean) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(viewVersionLocalChangesView.render(versionCommitForm, lesson, isClean));
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
