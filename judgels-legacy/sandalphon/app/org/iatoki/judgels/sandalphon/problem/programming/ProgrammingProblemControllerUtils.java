package org.iatoki.judgels.sandalphon.problem.programming;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.views.html.layouts.tabLayout;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.partner.ProgrammingProblemPartnerConfig;
import play.i18n.Messages;

public final class ProgrammingProblemControllerUtils {

    private ProgrammingProblemControllerUtils() {
        // prevent instantiation
    }

    public static void appendTabsLayout(LazyHtml content, ProblemService problemService, Problem problem) {
        ImmutableList.Builder<InternalLink> internalLinks = ImmutableList.builder();

        internalLinks.add(new InternalLink(Messages.get("problem.statement"), org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId())));

        if (isAllowedToManageGrading(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.programming.grading"), org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToGrading(problem.getId())));
        }

        if (isAllowedToSubmit(problemService, problem)) {
            internalLinks.add(new InternalLink(Messages.get("problem.programming.submission"), org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToSubmissions(problem.getId())));
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

    public static ProgrammingProblemPartnerConfig getPartnerConfig(ProblemService problemService, Problem problem) {
        return problemService.findProblemPartnerByProblemJidAndPartnerJid(problem.getJid(), IdentityUtils.getUserJid()).getChildConfig(ProgrammingProblemPartnerConfig.class);
    }

    public static boolean isAllowedToManageGrading(ProblemService problemService, Problem problem) {
        return ProblemControllerUtils.isAuthorOrAbove(problem) || (ProblemControllerUtils.isPartner(problemService, problem) && getPartnerConfig(problemService, problem).isAllowedToManageGrading());
    }

    public static boolean isAllowedToSubmit(ProblemService problemService, Problem problem) {
        return ProblemControllerUtils.isAuthorOrAbove(problem) || (ProblemControllerUtils.isPartner(problemService, problem) && getPartnerConfig(problemService, problem).isAllowedToSubmit());
    }
}
