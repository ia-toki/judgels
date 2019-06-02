package org.iatoki.judgels.sandalphon.problem.programming;

import judgels.sandalphon.api.problem.ProblemStatement;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
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
public final class ProgrammingProblemController extends AbstractProgrammingProblemController {

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
            ProblemStatement statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                    .text(ProgrammingProblemStatementUtils.getDefaultText(languageCode))
                    .build();
            problemService.updateStatement(null, problem.getJid(), languageCode, statement);
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
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createProgrammingProblemView.render(programmingProblemCreateForm, ProblemControllerUtils.getJustCreatedProblemSlug(), ProblemControllerUtils.getJustCreatedProblemAdditionalNote(), ProblemControllerUtils.getJustCreatedProblemInitLanguageCode()));
        template.setMainTitle(Messages.get("problem.programming.create"));
        template.markBreadcrumbLocation(Messages.get("problem.problems"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.setPageTitle("Programming Problem - Create");

        return renderTemplate(template);
    }
}
