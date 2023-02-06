package org.iatoki.judgels.sandalphon.problem.base;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.AbstractSandalphonController;
import org.iatoki.judgels.sandalphon.problem.base.version.html.versionLocalChangesWarningLayout;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractBaseProblemController extends AbstractSandalphonController {
    private final ProblemStore problemStore;

    protected AbstractBaseProblemController(ProblemStore problemStore) {
        this.problemStore = problemStore;
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
                availableLanguages = problemStore.getStatementAvailableLanguages(userJid, problem.getJid());

        if (currentLanguage == null
                || !availableLanguages.containsKey(currentLanguage)
                || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            return problemStore.getStatementDefaultLanguage(userJid, problem.getJid());
        }
        return currentLanguage;
    }

    protected String getEditorialLanguage(Http.Request req, Problem problem) {
        String userJid = getUserJid(req);
        String currentLanguage = getCurrentStatementLanguage(req);
        Map<String, StatementLanguageStatus>
                availableLanguages = problemStore.getEditorialAvailableLanguages(userJid, problem.getJid());

        if (currentLanguage == null
                || !availableLanguages.containsKey(currentLanguage)
                || availableLanguages.get(currentLanguage) == StatementLanguageStatus.DISABLED) {
            return problemStore.getEditorialDefaultLanguage(userJid, problem.getJid());
        }
        return currentLanguage;
    }

    @Override
    protected Result renderTemplate(HtmlTemplate template) {
        template.markBreadcrumbLocation("Problems", routes.ProblemController.index());
        return super.renderTemplate(template);
    }

    protected void appendVersionLocalChangesWarning(HtmlTemplate template, Problem problem) {
        String userJid = getUserJid(template.getRequest());
        if (problemStore.userCloneExists(userJid, problem.getJid())) {
            template.setWarning(versionLocalChangesWarningLayout.render(problem.getId(), null));
        }
    }
}
