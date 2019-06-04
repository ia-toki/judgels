package org.iatoki.judgels.jerahmeel.problemset;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.archive.Archive;
import org.iatoki.judgels.jerahmeel.archive.ArchiveNotFoundException;
import org.iatoki.judgels.jerahmeel.archive.ArchiveService;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.problemset.html.createProblemSetView;
import org.iatoki.judgels.jerahmeel.problemset.html.editProblemSetView;
import org.iatoki.judgels.jerahmeel.training.AbstractTrainingController;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;

public final class ProblemSetController extends AbstractProblemSetController {

    private final ArchiveService archiveService;
    private final ProblemSetService problemSetService;

    @Inject
    public ProblemSetController(ArchiveService archiveService, ProblemSetService problemSetService) {
        this.archiveService = archiveService;
        this.problemSetService = problemSetService;
    }

    @Authenticated(value = GuestView.class)
    public Result jumpToProblems(long problemSetId) {
        if (JerahmeelUtils.hasRole("admin")) {
            return redirect(org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewProblemSetProblems(problemSetId));
        }

        return redirect(org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewVisibleProblemSetProblems(problemSetId));
    }

    public Result jumpToSubmissions(long problemSetId) {
        if (!JerahmeelUtils.isGuest()) {
            return redirect(org.iatoki.judgels.jerahmeel.problemset.submission.programming.routes.ProblemSetProgrammingSubmissionController.viewSubmissions(problemSetId));
        }

        return redirect(org.iatoki.judgels.jerahmeel.problemset.submission.programming.routes.ProblemSetProgrammingSubmissionController.viewOwnSubmissions(problemSetId));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProblemSet(long archiveId) throws ArchiveNotFoundException {
        ProblemSetUpsertForm problemSetUpsertData = new ProblemSetUpsertForm();
        if (archiveId != 0) {
            Archive archive = archiveService.findArchiveById(archiveId);
            problemSetUpsertData.archiveJid = archive.getJid();
        }

        Form<ProblemSetUpsertForm> problemSetUpsertForm = Form.form(ProblemSetUpsertForm.class).fill(problemSetUpsertData);

        return showCreateProblemSet(archiveId, problemSetUpsertForm);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postCreateProblemSet() {
        Form<ProblemSetUpsertForm> problemSetUpsertForm = Form.form(ProblemSetUpsertForm.class).bindFromRequest();

        if (formHasErrors(problemSetUpsertForm)) {
            return showCreateProblemSet(0, problemSetUpsertForm);
        }

        ProblemSetUpsertForm problemSetUpsertData = problemSetUpsertForm.get();
        if (!archiveService.archiveExistsByJid(problemSetUpsertData.archiveJid)) {
            problemSetUpsertForm.reject(Messages.get("error.problemSet.archiveNotExist"));

            return showCreateProblemSet(0, problemSetUpsertForm);
        }

        Archive archive = archiveService.findArchiveByJid(problemSetUpsertData.archiveJid);
        problemSetService.createProblemSet(archive.getJid(), problemSetUpsertData.name, problemSetUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.jerahmeel.archive.routes.ArchiveController.viewArchives(archive.getId()));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblemSet(long problemSetId) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        ProblemSetUpsertForm problemSetUpsertData = new ProblemSetUpsertForm();
        problemSetUpsertData.archiveJid = problemSet.getParentArchive().getJid();
        problemSetUpsertData.name = problemSet.getName();
        problemSetUpsertData.description = problemSet.getDescription();

        Form<ProblemSetUpsertForm> problemSetUpsertForm = Form.form(ProblemSetUpsertForm.class).fill(problemSetUpsertData);

        return showEditProblemSet(problemSet, problemSetUpsertForm);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postEditProblemSet(long problemSetId) throws ProblemSetNotFoundException {
        ProblemSet problemSet = problemSetService.findProblemSetById(problemSetId);

        Form<ProblemSetUpsertForm> problemSetUpsertForm = Form.form(ProblemSetUpsertForm.class).bindFromRequest();

        if (formHasErrors(problemSetUpsertForm)) {
            return showEditProblemSet(problemSet, problemSetUpsertForm);
        }

        ProblemSetUpsertForm problemSetUpsertData = problemSetUpsertForm.get();
        if (!archiveService.archiveExistsByJid(problemSetUpsertData.archiveJid)) {
            problemSetUpsertForm.reject(Messages.get("error.problemSet.archiveNotExist"));

            return showEditProblemSet(problemSet, problemSetUpsertForm);
        }

        problemSetService.updateProblemSet(problemSet.getJid(), problemSetUpsertData.archiveJid, problemSetUpsertData.name, problemSetUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(org.iatoki.judgels.jerahmeel.problemset.problem.routes.ProblemSetProblemController.viewProblemSetProblems(problemSet.getId()));
    }

    private Result showCreateProblemSet(long archiveId, Form<ProblemSetUpsertForm> problemSetUpsertForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createProblemSetView.render(problemSetUpsertForm, archiveService.getAllArchives()));
        template.setMainTitle(Messages.get("archive.problemSet.create"));
        template.markBreadcrumbLocation(Messages.get("archive.problemSet.create"), routes.ProblemSetController.createProblemSet(archiveId));
        template.setPageTitle("Archive - Problem Set - Create");
        return renderTemplate(template);
    }

    private Result showEditProblemSet(ProblemSet problemSet, Form<ProblemSetUpsertForm> problemSetUpsertForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editProblemSetView.render(problemSet, problemSetUpsertForm, archiveService.getAllArchives()));
        template.setMainTitle(Messages.get("archive.problemSet.edit"));
        Archive archive = problemSet.getParentArchive();
        while (archive != null) {
            template.markBreadcrumbLocation(archive.getName(), org.iatoki.judgels.jerahmeel.archive.routes.ArchiveController.viewArchives(archive.getId()));
            archive = archive.getParentArchive();
        }
        template.setPageTitle("Archive - Problem Set - Edit");
        return renderTemplate(template);
    }
}
