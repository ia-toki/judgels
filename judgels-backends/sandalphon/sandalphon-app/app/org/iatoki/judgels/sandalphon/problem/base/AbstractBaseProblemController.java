package org.iatoki.judgels.sandalphon.problem.base;

import java.util.Map;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.SandalphonSessionUtils;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.version.html.versionLocalChangesWarningLayout;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractBaseProblemController extends AbstractSandalphonController {
    private final ProblemService problemService;

    protected AbstractBaseProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    protected String getStatementLanguage(Http.Request req, Problem problem) {
        String userJid = getUserJid(req);
        String currentLanguage = SandalphonSessionUtils.getCurrentStatementLanguage(req);
        Map<String, StatementLanguageStatus>
                availableLanguages = problemService.getAvailableLanguages(userJid, problem.getJid());

        if (currentLanguage == null
                || !availableLanguages.containsKey(currentLanguage)
                || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            return problemService.getDefaultLanguage(userJid, problem.getJid());
        }
        return currentLanguage;
    }

    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation("Problems", routes.ProblemController.index());
        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, ProblemService problemService, Problem problem) {
        String userJid = getUserJid(template.getRequest());
        if (problemService.userCloneExists(userJid, problem.getJid())) {
            template.setWarning(versionLocalChangesWarningLayout.render(problem.getId(), null));
        }
    }
}
