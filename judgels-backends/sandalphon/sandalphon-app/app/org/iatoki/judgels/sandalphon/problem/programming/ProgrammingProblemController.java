package org.iatoki.judgels.sandalphon.problem.programming;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import judgels.sandalphon.problem.programming.statement.ProgrammingProblemStatementUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.programming.html.createProgrammingProblemView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProgrammingProblemStore programmingProblemStore;
    private final ProblemTagStore problemTagStore;

    @Inject
    public ProgrammingProblemController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemStore programmingProblemStore,
            ProblemTagStore problemTagStore) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.programmingProblemStore = programmingProblemStore;
        this.problemTagStore = problemTagStore;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createProgrammingProblem(Http.Request req) {
        if (!wasProblemJustCreated(req)) {
            return badRequest();
        }

        Form<ProgrammingProblemCreateForm> form = formFactory.form(ProgrammingProblemCreateForm.class);

        return showCreateProgrammingProblem(req, form);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateProgrammingProblem(Http.Request req) {
        String actorJid = getUserJid(req);

        if (!wasProblemJustCreated(req)) {
            return badRequest();
        }

        Form<ProgrammingProblemCreateForm> form = formFactory.form(ProgrammingProblemCreateForm.class).bindFromRequest(req);

        if (formHasErrors(form)) {
            return showCreateProgrammingProblem(req, form);
        }

        String slug = getJustCreatedProblemSlug(req);
        String additionalNote = getJustCreatedProblemAdditionalNote(req);
        String languageCode = getJustCreatedProblemInitLanguage(req);

        ProgrammingProblemCreateForm data = form.get();

        Problem problem = problemStore.createProblem(ProblemType.PROGRAMMING, slug, additionalNote, languageCode);
        ProblemStatement statement = new ProblemStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                .text(ProgrammingProblemStatementUtils.getDefaultText(languageCode))
                .build();
        problemStore.updateStatement(null, problem.getJid(), languageCode, statement);
        programmingProblemStore.initProgrammingProblem(problem.getJid(), data.gradingEngineName);

        problemStore.initRepository(actorJid, problem.getJid());
        problemTagStore.refreshDerivedTags(problem.getJid());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.enterProblem(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(getJustCreatedProblemInitLanguage(req)))
                .removingFromSession(req, removeJustCreatedProblem());
    }

    public Result jumpToGrading(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.grading.routes.ProgrammingProblemGradingController.editGradingConfig(id));
    }

    public Result jumpToSubmissions(long id) {
        return redirect(org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.viewSubmissions(id));
    }

    private Result showCreateProgrammingProblem(Http.Request req, Form<ProgrammingProblemCreateForm> form) {
        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(createProgrammingProblemView.render(form, getJustCreatedProblemSlug(req), getJustCreatedProblemAdditionalNote(req), getJustCreatedProblemInitLanguage(req)));
        template.setMainTitle("Create programming problem");
        template.markBreadcrumbLocation("Problems", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.index());
        template.setPageTitle("Programming Problem - Create");

        return renderTemplate(template);
    }
}
