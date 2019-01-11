package org.iatoki.judgels.jerahmeel.problemset;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jerahmeel.archive.Archive;
import org.iatoki.judgels.jerahmeel.archive.ArchiveControllerUtils;
import org.iatoki.judgels.jerahmeel.archive.ArchiveNotFoundException;
import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.archive.ArchiveService;
import org.iatoki.judgels.jerahmeel.problemset.html.createProblemSetView;
import org.iatoki.judgels.jerahmeel.problemset.html.editProblemSetView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Stack;

public final class ProblemSetController extends AbstractJudgelsController {

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
        LazyHtml content = new LazyHtml(createProblemSetView.render(problemSetUpsertForm, archiveService.getAllArchives()));
        content.appendLayout(c -> headingLayout.render(Messages.get("archive.problemSet.create"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        ImmutableList.Builder<InternalLink> internalLinkBuilder = ImmutableList.builder();
        internalLinkBuilder.add(new InternalLink(Messages.get("archive.problemSet.create"), routes.ProblemSetController.createProblemSet(archiveId)));
        ArchiveControllerUtils.appendBreadcrumbsLayout(content, internalLinkBuilder.build());
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Archive - Problem Set - Create");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditProblemSet(ProblemSet problemSet, Form<ProblemSetUpsertForm> problemSetUpsertForm) {
        LazyHtml content = new LazyHtml(editProblemSetView.render(problemSet, problemSetUpsertForm, archiveService.getAllArchives()));
        content.appendLayout(c -> headingLayout.render(Messages.get("archive.problemSet.edit"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        ImmutableList.Builder<InternalLink> internalLinkBuilder = ImmutableList.builder();
        Archive archive = problemSet.getParentArchive();
        if (archive != null) {
            Stack<InternalLink> internalLinkStack = new Stack<>();
            Archive currentParent = archive;
            while (currentParent != null) {
                internalLinkStack.push(new InternalLink(currentParent.getName(), org.iatoki.judgels.jerahmeel.archive.routes.ArchiveController.viewArchives(currentParent.getId())));
                currentParent = currentParent.getParentArchive();
            }

            while (!internalLinkStack.isEmpty()) {
                internalLinkBuilder.add(internalLinkStack.pop());
            }
        }
        internalLinkBuilder.add(new InternalLink(Messages.get("archive.problemSet.edit"), routes.ProblemSetController.createProblemSet(archive.getId())));
        ArchiveControllerUtils.appendBreadcrumbsLayout(content, internalLinkBuilder.build());
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Archive - Problem Set - Edit");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }
}
