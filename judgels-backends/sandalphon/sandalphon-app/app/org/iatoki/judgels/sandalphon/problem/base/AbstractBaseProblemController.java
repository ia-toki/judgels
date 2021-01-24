package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.version.html.versionLocalChangesWarningLayout;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractBaseProblemController extends AbstractSandalphonController {
    private final ProblemService problemService;

    protected AbstractBaseProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    protected String getJustCreatedProblemSlug(Http.Request req) {
        return req.session().getOptional("problemSlug").orElse(null);
    }

    protected String getJustCreatedProblemAdditionalNote(Http.Request req) {
        return req.session().getOptional("problemAdditionalNote").orElse(null);
    }

    protected String getJustCreatedProblemInitLanguage(Http.Request req) {
        return req.session().getOptional("initLanguageCode").orElse(null);
    }

    protected boolean wasProblemJustCreated(Http.Request req) {
        return req.session().getOptional("problemSlug").isPresent()
                && req.session().getOptional("problemAdditionalNote").isPresent()
                && req.session().getOptional("initLanguageCode").isPresent();
    }

    protected Map<String, String> newJustCreatedProblem(String slug, String additionalNote, String initLanguage) {
        return ImmutableMap.of(
                "problemSlug", slug,
                "problemAdditionalNote", additionalNote,
                "initLanguageCode", initLanguage);
    }

    protected String[] removeJustCreatedProblem() {
        return new String[]{"problemSlug", "problemAdditionalNote", "initLanguageCode"};
    }

    protected String getStatementLanguage(Http.Request req, Problem problem) {
        String userJid = getUserJid(req);
        String currentLanguage = getCurrentStatementLanguage(req);
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
