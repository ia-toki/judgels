package org.iatoki.judgels.sandalphon.problem.base.version;

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
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.version.html.listVersionsView;
import org.iatoki.judgels.sandalphon.problem.base.version.html.viewVersionLocalChangesView;
import org.iatoki.judgels.sandalphon.resource.VersionCommitForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class ProblemVersionController extends AbstractProblemController {
    private final ProblemService problemService;
    private final ProfileService profileService;

    @Inject
    public ProblemVersionController(ProblemService problemService, ProfileService profileService) {
        this.problemService = problemService;
        this.profileService = profileService;
    }

    @Transactional(readOnly = true)
    public Result listVersionHistory(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isAllowedToViewVersionHistory(problemService, problem)) {
            return notFound();
        }

        List<GitCommit> versions = problemService.getVersions(IdentityUtils.getUserJid(), problem.getJid());

        Set<String> userJids = versions.stream().map(GitCommit::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = profileService.getProfiles(userJids);

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        boolean isAllowedToRestoreVersionHistory = isClean && ProblemControllerUtils.isAllowedToRestoreVersionHistory(problemService, problem);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listVersionsView.render(versions, problem.getId(), profilesMap, isAllowedToRestoreVersionHistory));
        template.markBreadcrumbLocation("History", routes.ProblemVersionController.listVersionHistory(problem.getId()));
        template.setPageTitle("Problem - Versions - History");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result restoreVersionHistory(long problemId, String hash) {
        Problem problem = checkFound(problemService.findProblemById(problemId));
        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());

        if (!isClean || !ProblemControllerUtils.isAllowedToRestoreVersionHistory(problemService, problem)) {
            return notFound();
        }

        problemService.restore(problem.getJid(), hash, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.ProblemVersionController.listVersionHistory(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewVersionLocalChanges(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class);

        return showViewVersionLocalChanges(versionCommitForm, problem, isClean);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCommitVersionLocalChanges(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        Form<VersionCommitForm> versionCommitForm = formFactory.form(VersionCommitForm.class).bindFromRequest();
        if (formHasErrors(versionCommitForm)) {
            boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
            return showViewVersionLocalChanges(versionCommitForm, problem, isClean);
        }

        VersionCommitForm versionCommitData = versionCommitForm.get();

        if (problemService.fetchUserClone(IdentityUtils.getUserJid(), problem.getJid())) {
            flash("localChangesError", "Your working copy has diverged from the master copy. Please update your working copy.");
        } else if (!problemService.commitThenMergeUserClone(IdentityUtils.getUserJid(), problem.getJid(), versionCommitData.title, versionCommitData.description, IdentityUtils.getIpAddress())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else if (!problemService.pushUserClone(IdentityUtils.getUserJid(), problem.getJid(), IdentityUtils.getIpAddress())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        } else {
            try {
                problemService.discardUserClone(IdentityUtils.getUserJid(), problem.getJid());
            } catch (IOException e) {
                // do nothing
            }
        }

        return redirect(routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result editVersionLocalChanges(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        problemService.fetchUserClone(IdentityUtils.getUserJid(), problem.getJid());

        if (!problemService.updateUserClone(IdentityUtils.getUserJid(), problem.getJid())) {
            flash("localChangesError", "Your local changes conflict with the master copy. Please remember, discard, and then reapply your local changes.");
        }

        return redirect(routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result discardVersionLocalChanges(long problemId) {
        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        try {
            problemService.discardUserClone(IdentityUtils.getUserJid(), problem.getJid());

            return redirect(routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
        } catch (IOException e) {
            return notFound();
        }
    }

    private Result showViewVersionLocalChanges(Form<VersionCommitForm> versionCommitForm, Problem problem, boolean isClean) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(viewVersionLocalChangesView.render(versionCommitForm, problem, isClean));
        template.markBreadcrumbLocation("Local changes", routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
        template.setPageTitle("Problem - Versions - Local changes");

        return renderTemplate(template, problemService, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.addSecondaryTab("Local changes", routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));

        if (ProblemControllerUtils.isAllowedToViewVersionHistory(problemService, problem)) {
            template.addSecondaryTab("History", routes.ProblemVersionController.listVersionHistory(problem.getId()));
        }

        template.markBreadcrumbLocation("Versions", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
