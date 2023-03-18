package judgels.michael.problem.programming.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.problem.programming.BaseProgrammingProblemResource;
import judgels.michael.problem.programming.grading.LanguageRestrictionAdapter;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;

@Path("/problems/programming/{problemId}/statements")
public class ProgrammingProblemStatementResource extends BaseProgrammingProblemResource {
    @Inject public ProgrammingProblemStatementResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View viewStatement(@Context HttpServletRequest req, @PathParam("problemId") int problemId) {
        Actor actor = actorChecker.check(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(roleChecker.canView(actor, problem));

        Set<String> enabledLanguages = problemStore.getStatementEnabledLanguages(actor.getUserJid(), problem.getJid());
        String language = resolveStatementLanguage(req, actor, problem, enabledLanguages);
        ProblemStatement statement = problemStore.getStatement(actor.getUserJid(), problem.getJid(), language);

        String gradingEngine = programmingProblemStore.getGradingEngine(actor.getUserJid(), problem.getJid());
        GradingConfig gradingConfig = programmingProblemStore.getGradingConfig(actor.getUserJid(), problem.getJid());
        LanguageRestriction gradingLanguageRestriction = programmingProblemStore.getLanguageRestriction(actor.getUserJid(), problem.getJid());
        Set<String> allowedGradingLanguages = LanguageRestrictionAdapter.getAllowedLanguages(gradingLanguageRestriction);

        boolean isClean = !problemStore.userCloneExists(actor.getUserJid(), problem.getJid());

        String reasonNotAllowedToSubmit = null;
        if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        HtmlTemplate template = newProblemStatementTemplate(actor, problem);
        template.setActiveSecondaryTab("view");
        return new ViewStatementView(template, statement, language, enabledLanguages, gradingConfig, gradingEngine, allowedGradingLanguages, reasonNotAllowedToSubmit);
    }
}
