package org.iatoki.judgels.sandalphon.problem.bundle;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.partner.BundleProblemPartnerConfig;
import play.i18n.Messages;

import java.util.Set;

public final class BundleProblemControllerUtils {

    private BundleProblemControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabsLayout(LazyHtml content, ProblemService problemService, Problem problem) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("problem.statement"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId())));

        if (isAllowedToManageItems(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.bundle.item"), routes.BundleProblemController.jumpToItems(problem.getId())));
        }

        if (isAllowedToSubmit(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.bundle.submission"), routes.BundleProblemController.jumpToSubmissions(problem.getId())));
        }

        if (ProblemControllerUtils.isAuthorOrAbove(problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.partner"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId())));
        }

        internalLinks.add(new InternalLink(Messages.get("problem.version"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId())));

        if (ProblemControllerUtils.isAllowedToManageClients(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.client"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToClients(problem.getId())));
        }

        content.appendLayout(c -> tabLayout.render(internalLinks.build(), c));
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
