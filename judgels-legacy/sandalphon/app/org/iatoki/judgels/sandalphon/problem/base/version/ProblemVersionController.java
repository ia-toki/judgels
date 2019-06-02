package org.iatoki.judgels.sandalphon.problem.base.version;

import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.activity.SandalphonActivityKeys;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.resource.VersionCommitForm;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.version.html.listVersionsView;
import org.iatoki.judgels.sandalphon.problem.base.version.html.viewVersionLocalChangesView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ProblemVersionController extends AbstractProblemController {

    private static final String COMMIT = "commit";
    private static final String PROBLEM = "problem";

    private final ProblemService problemService;

    @Inject
    public ProblemVersionController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result listVersionHistory(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToViewVersionHistory(problemService, problem)) {
            return notFound();
        }

        List<GitCommit> versions = problemService.getVersions(IdentityUtils.getUserJid(), problem.getJid());
        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
        boolean isAllowedToRestoreVersionHistory = isClean && ProblemControllerUtils.isAllowedToRestoreVersionHistory(problemService, problem);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listVersionsView.render(versions, problem.getId(), isAllowedToRestoreVersionHistory));
        template.markBreadcrumbLocation(Messages.get("problem.version.history"), routes.ProblemVersionController.listVersionHistory(problem.getId()));
        template.setPageTitle("Problem - Versions - History");

        return renderTemplate(template, problemService, problem);
    }

    @Transactional(readOnly = true)
    public Result restoreVersionHistory(long problemId, String hash) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());

        if (!isClean || !ProblemControllerUtils.isAllowedToRestoreVersionHistory(problemService, problem)) {
            return notFound();
        }

        problemService.restore(problem.getJid(), hash, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(SandalphonActivityKeys.RESTORE.construct(PROBLEM, problem.getJid(), problem.getSlug(), COMMIT, null, hash));

        return redirect(routes.ProblemVersionController.listVersionHistory(problem.getId()));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result viewVersionLocalChanges(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());

        Form<VersionCommitForm> versionCommitForm = Form.form(VersionCommitForm.class);

        return showViewVersionLocalChanges(versionCommitForm, problem, isClean);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCommitVersionLocalChanges(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        Form<VersionCommitForm> versionCommitForm = Form.form(VersionCommitForm.class).bindFromRequest();
        if (formHasErrors(versionCommitForm)) {
            boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());
            return showViewVersionLocalChanges(versionCommitForm, problem, isClean);
        }

        VersionCommitForm versionCommitData = versionCommitForm.get();

        if (problemService.fetchUserClone(IdentityUtils.getUserJid(), problem.getJid())) {
            flash("localChangesError", Messages.get("problem.version.local.cantCommit"));
        } else if (!problemService.commitThenMergeUserClone(IdentityUtils.getUserJid(), problem.getJid(), versionCommitData.title, versionCommitData.description, IdentityUtils.getIpAddress())) {
            flash("localChangesError", Messages.get("problem.version.local.cantMerge"));
        } else if (!problemService.pushUserClone(IdentityUtils.getUserJid(), problem.getJid(), IdentityUtils.getIpAddress())) {
            flash("localChangesError", Messages.get("problem.version.local.cantMerge"));
        } else {
            try {
                problemService.discardUserClone(IdentityUtils.getUserJid(), problem.getJid());
            } catch (IOException e) {
                // do nothing
            }
        }

        SandalphonControllerUtils.getInstance().addActivityLog(SandalphonActivityKeys.COMMIT.construct(PROBLEM, problem.getJid(), problem.getSlug(), COMMIT, null, versionCommitData.title));

        return redirect(routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result editVersionLocalChanges(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isPartnerOrAbove(problemService, problem)) {
            return notFound();
        }

        problemService.fetchUserClone(IdentityUtils.getUserJid(), problem.getJid());

        if (!problemService.updateUserClone(IdentityUtils.getUserJid(), problem.getJid())) {
            flash("localChangesError", Messages.get("problem.version.local.cantMerge"));
        }

        return redirect(routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
    }

    @Transactional(readOnly = true)
    public Result discardVersionLocalChanges(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

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
        template.markBreadcrumbLocation(Messages.get("problem.version.local"), routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));
        template.setPageTitle("Problem - Versions - Local Changes");

        return renderTemplate(template, problemService, problem);
    }

    protected Result renderTemplate(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.addSecondaryTab(Messages.get("problem.version.local"), routes.ProblemVersionController.viewVersionLocalChanges(problem.getId()));

        if (ProblemControllerUtils.isAllowedToViewVersionHistory(problemService, problem)) {
            template.addSecondaryTab(Messages.get("problem.version.history"), routes.ProblemVersionController.listVersionHistory(problem.getId()));
        }

        template.markBreadcrumbLocation(Messages.get("problem.version"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));

        return super.renderTemplate(template, problemService, problem);
    }
}
