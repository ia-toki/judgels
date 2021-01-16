package org.iatoki.judgels.sandalphon.problem.bundle;

import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.BundleProblemPartnerConfig;

import java.util.Set;

public final class BundleProblemControllerUtils {

    private BundleProblemControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabs(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.addMainTab("Statements", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        if (BundleProblemControllerUtils.isAllowedToManageItems(problemService, problem)) {
            template.addMainTab("Items", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToItems(problem.getId()));
        }

        if (BundleProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            template.addMainTab("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToSubmissions(problem.getId()));
        }

        if (ProblemControllerUtils.isAuthorOrAbove(problem)) {
            template.addMainTab("Partners", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));
        }

        template.addMainTab("Versions", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));
    }

    public static BundleProblemPartnerConfig getPartnerConfig(ProblemService problemService, Problem problem) {
        return problemService.findProblemPartnerByProblemJidAndPartnerJid(problem.getJid(), IdentityUtils.getUserJid()).getChildConfig(BundleProblemPartnerConfig.class);
    }

    public static boolean isAllowedToManageItems(ProblemService problemService, Problem problem) {
        return ProblemControllerUtils.isAuthorOrAbove(problem) || (ProblemControllerUtils.isPartner(problemService, problem) && getPartnerConfig(problemService, problem).isAllowedToManageItems());
    }

    public static boolean isAllowedToUpdateItemInLanguage(ProblemService problemService, Problem problem) {
        if (!isAllowedToManageItems(problemService, problem)) {
            return false;
        }

        if (ProblemControllerUtils.isAuthorOrAbove(problem)) {
            return true;
        }

        if (!ProblemControllerUtils.isPartner(problemService, problem)) {
            return false;
        }

        String language = ProblemControllerUtils.getCurrentStatementLanguage();

        Set<String> allowedLanguages = ProblemControllerUtils.getPartnerConfig(problemService, problem).getAllowedStatementLanguagesToUpdate();

        if ((allowedLanguages == null) || allowedLanguages.contains(language)) {
            return true;
        }

        String firstLanguage = allowedLanguages.iterator().next();

        ProblemControllerUtils.setCurrentStatementLanguage(firstLanguage);
        return true;
    }

    public static boolean isAllowedToSubmit(ProblemService problemService, Problem problem) {
        return ProblemControllerUtils.isAuthorOrAbove(problem) || (ProblemControllerUtils.isPartner(problemService, problem) && getPartnerConfig(problemService, problem).isAllowedToSubmit());
    }
}
