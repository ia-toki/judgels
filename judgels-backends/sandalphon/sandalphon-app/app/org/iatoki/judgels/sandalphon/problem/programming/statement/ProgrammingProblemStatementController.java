package org.iatoki.judgels.sandalphon.problem.programming.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestrictionAdapter;
import org.iatoki.judgels.sandalphon.resource.html.katexView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class ProgrammingProblemStatementController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProgrammingProblemStore programmingProblemStore;

    @Inject
    public ProgrammingProblemStatementController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemStore programmingProblemStore) {

        super(problemStore, problemRoleChecker);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.programmingProblemStore = programmingProblemStore;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToViewStatement(req, problem, language));

        ProblemStatement statement = problemStore.getStatement(actorJid, problem.getJid(), language);

        String engine = programmingProblemStore.getGradingEngine(actorJid, problem.getJid());
        GradingConfig config = programmingProblemStore.getGradingConfig(actorJid, problem.getJid());
        LanguageRestriction languageRestriction = programmingProblemStore.getLanguageRestriction(actorJid, problem.getJid());
        Set<String> allowedLanguageNames = LanguageRestrictionAdapter.getFinalAllowedLanguageNames(ImmutableList.of(languageRestriction));

        boolean isAllowedToSubmitByPartner = problemRoleChecker.isAllowedToSubmit(req, problem);
        boolean isClean = !problemStore.userCloneExists(actorJid, problem.getJid());

        String reasonNotAllowedToSubmit = null;

        if (!isAllowedToSubmitByPartner) {
            reasonNotAllowedToSubmit = "You are not allowed to submit.";
        } else if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(engine).renderViewStatement(org.iatoki.judgels.sandalphon.problem.programming.submission.routes.ProgrammingProblemSubmissionController.postSubmit(problemId).url(), statement, config, engine, allowedLanguageNames, reasonNotAllowedToSubmit));
        template.addAdditionalScript(katexView.render());

        Set<String> allowedLanguages = problemRoleChecker.getAllowedStatementLanguagesToView(req, problem);

        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));
        template.markBreadcrumbLocation("View statement", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
        template.setPageTitle("Problem - View statement");

        return renderStatementTemplate(template, problem)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }
}
