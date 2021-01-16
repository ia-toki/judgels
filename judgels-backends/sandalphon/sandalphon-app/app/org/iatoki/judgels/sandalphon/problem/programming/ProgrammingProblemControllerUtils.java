package org.iatoki.judgels.sandalphon.problem.programming;

import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.partner.ProgrammingProblemPartnerConfig;

public final class ProgrammingProblemControllerUtils {

    private ProgrammingProblemControllerUtils() {
        // prevent instantiation
    }

    public static  void appendTabs(HtmlTemplate template, ProblemService problemService, Problem problem) {
        template.addMainTab("Statements", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        if (ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            template.addMainTab("Grading", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToGrading(problem.getId()));
        }

        if (ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem)) {
            template.addMainTab("Submissions", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToSubmissions(problem.getId()));
        }

        if (ProblemControllerUtils.isAuthorOrAbove(problem)) {
            template.addMainTab("Partners", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));
        }

        template.addMainTab("Versions", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));
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
