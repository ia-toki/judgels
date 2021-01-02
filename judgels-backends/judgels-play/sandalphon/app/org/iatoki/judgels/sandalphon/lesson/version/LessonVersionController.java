package org.iatoki.judgels.sandalphon.lesson.version;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.lesson.Lesson;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.lesson.AbstractLessonController;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonNotFoundException;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.lesson.version.html.listVersionsView;
import org.iatoki.judgels.sandalphon.lesson.version.html.viewVersionLocalChangesView;
import org.iatoki.judgels.sandalphon.resource.VersionCommitForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class LessonVersionController extends AbstractLessonController {
    private final LessonService lessonService;

    @Inject
    public LessonVersionController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    public Result listVersionHistory(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToViewVersionHistory(lessonService, lesson)) {
            return notFound();
        }

        List<GitCommit> versions = lessonService.getVersions(IdentityUtils.getUserJid(), lesson.getJid());
        boolean isClean = !lessonService.userCloneExists(IdentityUtils.getUserJid(), lesson.getJid());
        boolean isAllowedToRestoreVersionHistory = isClean && LessonControllerUtils.isAllowedToRestoreVersionHistory(lessonService, lesson);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listVersionsView.render(versions, lesson.getId(), isAllowedToRestoreVersionHistory));
        template.markBreadcrumbLocation("History", routes.LessonVersionController.listVersionHistory(lesson.getId()));
        template.setPageTitle("Lesson - Versions - History");

        return renderTemplate(template, lessonService, lesson);
    }

    @Transactional(readOnly = true)
    public Result restoreVersionHistory(long lessonId, String hash) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);
        boolean isClean = !lessonService.userCloneExists(IdentityUtils.getUserJid(), lesson.getJid());

        if (!isClean || !LessonControllerUtils.isAllowedToRestoreVersionHistory(lessonService, lesson)) {
            return notFound();
        }

        lessonService.restore(lesson.getJid(), hash, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.LessonVersionController.listVersionHistory(lesson.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewVersionLocalChanges(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isPartnerOrAbove(lessonService, lesson)) {
            return notFound();
        }

        boolean isClean = !lessonService.userCloneExists(IdentityUtils.getUserJid(), lesson.getJid());

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class);

        return showViewVersionLocalChanges(versionCommitForm, lesson, isClean);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCommitVersionLocalChanges(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isPartnerOrAbove(lessonService, lesson)) {
            return notFound();
        }

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class).bindFromRequest();
        if (formHasErrors(versionCommitForm)) {
            boolean isClean = !lessonService.userCloneExists(IdentityUtils.getUserJid(), lesson.getJid());
            return showViewVersionLocalChanges(versionCommitForm, lesson, isClean);
        }

        VersionCommitForm versionCommitData = versionCommitForm.get();

        if (lessonService.fetchUserClone(IdentityUtils.getUserJid(), lesson.getJid())) {
            flash("localChangesError", "Your working copy has diverged from the master copy. Please update your working copy.");
        } else if (!lessonService.commitThenMergeUserClone(IdentityUtils.getUserJid(), lesson.getJid(), versionCommitData.title, versionCommitData.description, IdentityUtils.getIpAddress())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else if (!lessonService.pushUserClone(IdentityUtils.getUserJid(), lesson.getJid(), IdentityUtils.getIpAddress())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else {
            try {
                lessonService.discardUserClone(IdentityUtils.getUserJid(), lesson.getJid());
            } catch (IOException e) {
                // do nothing
            }
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
    }

    @Transactional(readOnly = true)
    public Result editVersionLocalChanges(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isPartnerOrAbove(lessonService, lesson)) {
            return notFound();
        }

        lessonService.fetchUserClone(IdentityUtils.getUserJid(), lesson.getJid());

        if (!lessonService.updateUserClone(IdentityUtils.getUserJid(), lesson.getJid())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        }

        return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
    }

    @Transactional(readOnly = true)
    public Result discardVersionLocalChanges(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isPartnerOrAbove(lessonService, lesson)) {
            return notFound();
        }

        try {
            lessonService.discardUserClone(IdentityUtils.getUserJid(), lesson.getJid());

            return redirect(routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
        } catch (IOException e) {
            return notFound();
        }
    }

    private Result showViewVersionLocalChanges(Form<VersionCommitForm> versionCommitForm, Lesson lesson, boolean isClean) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewVersionLocalChangesView.render(versionCommitForm, lesson, isClean));
        template.markBreadcrumbLocation("Local changes", routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));
        template.setPageTitle("Lesson - Versions - Local changes");

        return renderTemplate(template, lessonService, lesson);
    }

    protected Result renderTemplate(HtmlTemplate template, LessonService lessonService, Lesson lesson) {
        template.addSecondaryTab("Local changes", routes.LessonVersionController.viewVersionLocalChanges(lesson.getId()));

        if (LessonControllerUtils.isAllowedToViewVersionHistory(lessonService, lesson)) {
            template.addSecondaryTab("History", routes.LessonVersionController.listVersionHistory(lesson.getId()));
        }

        template.markBreadcrumbLocation("Versions", org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToVersions(lesson.getId()));

        return super.renderTemplate(template, lessonService, lesson);
    }
}
