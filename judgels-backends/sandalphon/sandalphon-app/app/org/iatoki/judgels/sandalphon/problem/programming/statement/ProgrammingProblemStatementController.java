package org.iatoki.judgels.sandalphon.problem.programming.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.gabriel.engines.GradingEngineRegistry;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestrictionAdapter;
import org.iatoki.judgels.sandalphon.resource.html.katexView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemStatementController extends AbstractProblemController {
    private final ProblemService problemService;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ProgrammingProblemStatementController(
            ProblemService problemService,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemService programmingProblemService) {

        super(problemService, problemRoleChecker);
        this.problemService = problemService;
        this.problemRoleChecker = problemRoleChecker;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToViewStatement(req, problem, language));

        ProblemStatement statement;
        try {
            statement = problemService.getStatement(actorJid, problem.getJid(), language);
        } catch (IOException e) {
            statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(language))
                    .text(ProgrammingProblemStatementUtils.getDefaultText(language))
                    .build();
        }

        String engine;
        try {
            engine = programmingProblemService.getGradingEngine(actorJid, problem.getJid());
        } catch (IOException e) {
            engine = GradingEngineRegistry.getInstance().getDefault();
        }

        GradingConfig config;
        try {
            config = programmingProblemService.getGradingConfig(actorJid, problem.getJid());
        } catch (IOException e) {
            config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
        }
        LanguageRestriction languageRestriction;
        try {
            languageRestriction = programmingProblemService.getLanguageRestriction(actorJid, problem.getJid());
        } catch (IOException e) {
            languageRestriction = LanguageRestriction.noRestriction();
        }
        Set<String> allowedLanguageNames = LanguageRestrictionAdapter.getFinalAllowedLanguageNames(ImmutableList.of(languageRestriction));

        boolean isAllowedToSubmitByPartner = problemRoleChecker.isAllowedToSubmit(req, problem);
        boolean isClean = !problemService.userCloneExists(actorJid, problem.getJid());

        String reasonNotAllowedToSubmit = null;

        if (!isAllowedToSubmitByPartner) {
            reasonNotAllowedToSubmit = "You are not allowed to submit.";
        } else if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).renderViewStatement(org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.postSubmit(problemId).absoluteURL(request(), request().secure()), statement, config, engine, allowedLanguageNames, reasonNotAllowedToSubmit));
        template.addAdditionalScript(katexView.render());

        Set<String> allowedLanguages = problemRoleChecker.getAllowedLanguagesToView(req, problem);

        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("View statement", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
        template.setPageTitle("Problem - View statement");

        return renderStatementTemplate(template, problem)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }
}
