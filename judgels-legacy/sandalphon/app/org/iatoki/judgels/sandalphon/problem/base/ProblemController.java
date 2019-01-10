package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.play.views.html.layouts.subtabLayout;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.html.createProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.editProblemView;
import org.iatoki.judgels.sandalphon.problem.base.html.listProblemsView;
import org.iatoki.judgels.sandalphon.problem.base.html.viewProblemView;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ProblemController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String PROBLEM = "problem";

    private final ProblemService problemService;

    @Inject
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listProblems(0, "timeUpdate", "desc", "");
    }

    @Transactional(readOnly = true)
    public Result listProblems(long pageIndex, String sortBy, String orderBy, String filterString) {
        Page<Problem> pageOfProblems = problemService.getPageOfProblems(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString, IdentityUtils.getUserJid(), SandalphonControllerUtils.getInstance().isAdmin());

        LazyHtml content = new LazyHtml(listProblemsView.render(pageOfProblems, sortBy, orderBy, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("problem.list"), new InternalLink(Messages.get("commons.create"), routes.ProblemController.createProblem()), c));

        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
              new InternalLink(Messages.get("problem.problems"), routes.ProblemController.index())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problems");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProblem() {
        Form<ProblemCreateForm> problemCreateForm = Form.form(ProblemCreateForm.class);

        return showCreateProblem(problemCreateForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProblem() {
        Form<ProblemCreateForm> problemCreateForm = Form.form(ProblemCreateForm.class).bindFromRequest();

        if (formHasErrors(problemCreateForm)) {
            return showCreateProblem(problemCreateForm);
        }

        if (problemService.problemExistsBySlug(problemCreateForm.get().slug)) {
            problemCreateForm.reject("slug", Messages.get("error.problem.slugExists"));
        }

        ProblemCreateForm problemCreateData = problemCreateForm.get();
        ProblemControllerUtils.setJustCreatedProblem(problemCreateData.slug, problemCreateData.additionalNote, problemCreateData.initLanguageCode);

        if (problemCreateData.type.equals(ProblemType.PROGRAMMING.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.createProgrammingProblem());
        } else if (problemCreateData.type.equals(ProblemType.BUNDLE.name())) {
            return redirect(org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.createBundleProblem());
        }

        return internalServerError();
    }

    public Result enterProblem(long problemId) {
        return redirect(routes.ProblemController.jumpToStatement(problemId));
    }

    public Result jumpToStatement(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
    }

    public Result jumpToVersions(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.version.routes.ProblemVersionController.viewVersionLocalChanges(problemId));
    }

    public Result jumpToPartners(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.partner.routes.ProblemPartnerController.viewPartners(problemId));
    }

    public Result jumpToClients(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.base.client.routes.ProblemClientController.editClientProblems(problemId));
    }

    @Transactional(readOnly = true)
    public Result viewProblem(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        if (!ProblemControllerUtils.isAllowedToViewStatement(problemService, problem)) {
            return notFound();
        }

        LazyHtml content = new LazyHtml(viewProblemView.render(problem));
        appendSubtabs(content, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        content.appendLayout(c -> headingWithActionLayout.render("#" + problem.getId() + ": " + problem.getSlug(), new InternalLink(Messages.get("problem.enter"), routes.ProblemController.enterProblem(problem.getId())), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                ProblemControllerUtils.getProblemBreadcrumbsBuilder(problem)
                        .add(new InternalLink(Messages.get("problem.view"), routes.ProblemController.viewProblem(problem.getId())))
                        .build()
        );
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - View");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editProblem(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return redirect(routes.ProblemController.viewProblem(problem.getId()));
        }

        ProblemEditForm problemEditData = new ProblemEditForm();
        problemEditData.slug = problem.getSlug();
        problemEditData.additionalNote = problem.getAdditionalNote();

        Form<ProblemEditForm> problemEditForm = Form.form(ProblemEditForm.class).fill(problemEditData);

        return showEditProblem(problemEditForm, problem);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditProblem(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            return notFound();
        }

        Form<ProblemEditForm> problemEditForm = Form.form(ProblemEditForm.class).bindFromRequest();

        if (formHasErrors(problemEditForm)) {
            return showEditProblem(problemEditForm, problem);
        }

        if (!problem.getSlug().equals(problemEditForm.get().slug) && problemService.problemExistsBySlug(problemEditForm.get().slug)) {
            problemEditForm.reject("slug", Messages.get("error.problem.slugExists"));
        }

        ProblemEditForm problemEditData = problemEditForm.get();
        problemService.updateProblem(problem.getJid(), problemEditData.slug, problemEditData.additionalNote, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!problem.getSlug().equals(problemEditData.slug)) {
            SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(PROBLEM, problem.getJid(), problem.getSlug(), problemEditData.slug));
        }
        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(PROBLEM, problem.getJid(), problemEditData.slug));

        return redirect(routes.ProblemController.viewProblem(problem.getId()));
    }

    public Result switchLanguage(long problemId) {
        String languageCode = DynamicForm.form().bindFromRequest().get("langCode");
        ProblemControllerUtils.setCurrentStatementLanguage(languageCode);

        return redirect(request().getHeader("Referer"));
    }

    private Result showCreateProblem(Form<ProblemCreateForm> problemCreateForm) {
        LazyHtml content = new LazyHtml(createProblemView.render(problemCreateForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("problem.create"), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("problem.problems"), routes.ProblemController.index()),
                new InternalLink(Messages.get("problem.create"), routes.ProblemController.createProblem())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditProblem(Form<ProblemEditForm> problemEditForm, Problem problem) {
        LazyHtml content = new LazyHtml(editProblemView.render(problemEditForm, problem));
        appendSubtabs(content, problem);
        ProblemControllerUtils.appendVersionLocalChangesWarningLayout(content, problemService, problem);
        content.appendLayout(c -> headingWithActionLayout.render("#" + problem.getId() + ": " + problem.getSlug(), new InternalLink(Messages.get("problem.enter"), routes.ProblemController.enterProblem(problem.getId())), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                ProblemControllerUtils.getProblemBreadcrumbsBuilder(problem)
                .add(new InternalLink(Messages.get("problem.update"), routes.ProblemController.editProblem(problem.getId())))
                .build()
        );
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Problem - Update");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private void appendSubtabs(LazyHtml content, Problem problem) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("commons.view"), routes.ProblemController.viewProblem(problem.getId())));

        if (ProblemControllerUtils.isAllowedToUpdateProblem(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("commons.update"), routes.ProblemController.editProblem(problem.getId())));
        }

        content.appendLayout(c -> subtabLayout.render(internalLinks.build(), c));
    }
}
