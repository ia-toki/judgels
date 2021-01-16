package org.iatoki.judgels.sandalphon.problem.programming;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.programming.html.createProgrammingProblemView;
import org.iatoki.judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemController extends AbstractProgrammingProblemController {
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

        Form<ProgrammingProblemCreateForm> form = formFactory.form(ProgrammingProblemCreateForm.class);

        return showCreateProgrammingProblem(form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProgrammingProblem() {
        if (!ProblemControllerUtils.wasProblemJustCreated()) {
            return badRequest();
        }

        Form<ProgrammingProblemCreateForm> programmingProblemCreateForm = formFactory.form(ProgrammingProblemCreateForm.class).bindFromRequest();

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
            ProblemStatement statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                    .text(ProgrammingProblemStatementUtils.getDefaultText(languageCode))
                    .build();
            problemService.updateStatement(null, problem.getJid(), languageCode, statement);
            programmingProblemService.initProgrammingProblem(problem.getJid(), programmingProblemCreateData.gradingEngineName);
        } catch (IOException e) {
            return showCreateProgrammingProblem(programmingProblemCreateForm.withGlobalError("Error creating programming problem."));
        }

        problemService.initRepository(IdentityUtils.getUserJid(), problem.getJid());

        ProblemControllerUtils.setCurrentStatementLanguage(ProblemControllerUtils.getJustCreatedProblemInitLanguageCode());
        ProblemControllerUtils.removeJustCreatedProblem();

        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.enterProblem(problem.getId()));
    }

    public Result jumpToGrading(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.grading.routes.ProgrammingProblemGradingController.editGradingConfig(id));
    }

    public Result jumpToSubmissions(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmissions(id));
    }

    private Result showCreateProgrammingProblem(Form<ProgrammingProblemCreateForm> programmingProblemCreateForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createProgrammingProblemView.render(programmingProblemCreateForm, ProblemControllerUtils.getJustCreatedProblemSlug(), ProblemControllerUtils.getJustCreatedProblemAdditionalNote(), ProblemControllerUtils.getJustCreatedProblemInitLanguageCode()));
        template.setMainTitle("Create programming problem");
        template.markBreadcrumbLocation("Problems", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.setPageTitle("Programming Problem - Create");

        return renderTemplate(template);
    }
}
