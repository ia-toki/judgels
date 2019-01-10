package org.iatoki.judgels.sandalphon.problem.programming;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatement;
import org.iatoki.judgels.sandalphon.problem.base.ProblemType;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.programming.html.createProgrammingProblemView;
import org.iatoki.judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class ProgrammingProblemController extends AbstractJudgelsController {

    private static final String PROBLEM = "problem";

    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ProgrammingProblemController(ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProgrammingProblem() {
        if (!ProblemControllerUtils.wasProblemJustCreated()) {
            return badRequest();
        }

        Form<ProgrammingProblemCreateForm> form = Form.form(ProgrammingProblemCreateForm.class);

        return showCreateProgrammingProblem(form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProgrammingProblem() {
        if (!ProblemControllerUtils.wasProblemJustCreated()) {
            return badRequest();
        }

        Form<ProgrammingProblemCreateForm> programmingProblemCreateForm = Form.form(ProgrammingProblemCreateForm.class).bindFromRequest();

        if (formHasErrors(programmingProblemCreateForm)) {
            return showCreateProgrammingProblem(programmingProblemCreateForm);
        }

        String slug = ProblemControllerUtils.getJustCreatedProblemSlug();
        String additionalNote = ProblemControllerUtils.getJustCreatedProblemAdditionalNote();
        String languageCode = ProblemControllerUtils.getJustCreatedProblemInitLanguageCode();

        ProgrammingProblemCreateForm programmingProblemCreateData = programmingProblemCreateForm.get();

        Problem problem;
        try {
            problem = problemService.createProblem(ProblemType.PROGRAMMING, slug, additionalNote, languageCode, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            problemService.updateStatement(null, problem.getJid(), languageCode, new ProblemStatement(ProblemStatementUtils.getDefaultTitle(languageCode), ProgrammingProblemStatementUtils.getDefaultText(languageCode)));
            programmingProblemService.initProgrammingProblem(problem.getJid(), programmingProblemCreateData.gradingEngineName);
        } catch (IOException e) {
            programmingProblemCreateForm.reject("problem.programming.error.cantCreate");
            return showCreateProgrammingProblem(programmingProblemCreateForm);
        }

        problemService.initRepository(IdentityUtils.getUserJid(), problem.getJid());

        ProblemControllerUtils.setCurrentStatementLanguage(ProblemControllerUtils.getJustCreatedProblemInitLanguageCode());
        ProblemControllerUtils.removeJustCreatedProblem();

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(PROBLEM, problem.getJid(), problem.getSlug()));

        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.enterProblem(problem.getId()));
    }

    public Result jumpToGrading(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.grading.routes.ProgrammingProblemGradingController.editGradingConfig(id));
    }

    public Result jumpToSubmissions(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmissions(id));
    }

    private Result showCreateProgrammingProblem(Form<ProgrammingProblemCreateForm> programmingProblemCreateForm) {
        LazyHtml content = new LazyHtml(createProgrammingProblemView.render(programmingProblemCreateForm, ProblemControllerUtils.getJustCreatedProblemSlug(), ProblemControllerUtils.getJustCreatedProblemAdditionalNote(), ProblemControllerUtils.getJustCreatedProblemInitLanguageCode()));
        content.appendLayout(c -> headingLayout.render(Messages.get("problem.programming.create"), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("problem.problems"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Programming Problem - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }
}
