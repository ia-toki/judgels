package org.iatoki.judgels.sandalphon.problem.base;

import java.util.Set;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.statementLanguageSelectionLayout;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;

public abstract class AbstractProblemController extends AbstractBaseProblemController {
    private final ProblemRoleChecker problemRoleChecker;

    protected AbstractProblemController(ProblemStore problemStore, ProblemRoleChecker problemRoleChecker) {
        super(problemStore);
        this.problemRoleChecker = problemRoleChecker;
    }

    protected Result renderTemplate(HtmlTemplate template, Problem problem) {
        appendTabs(template, problem);
        appendVersionLocalChangesWarning(template, problem);
        appendTitle(template, problem);

        return super.renderTemplate(template);
    }

    protected void appendTitle(HtmlTemplate template, Problem problem) {
        template.setMainTitle("#" + problem.getId() + ": " + problem.getSlug());

        if (problemRoleChecker.isAllowedToUpdateProblem(template.getRequest(), problem)) {
            template.addMainButton("Update problem", routes.ProblemController.editProblem(problem.getId()));
        } else {
            template.addMainButton("View problem", routes.ProblemController.viewProblem(problem.getId()));
        }
    }

    protected void appendTabs(HtmlTemplate template, Problem problem) {
        if (problem.getType().equals(ProblemType.PROGRAMMING)) {
            appendProgrammingTabs(template, problem);
        } else if (problem.getType().equals(ProblemType.BUNDLE)) {
            appendBundleTabs(template, problem);
        }
    }

    protected void appendProgrammingTabs(HtmlTemplate template, Problem problem) {
        template.addMainTab("Statements", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        if (problemRoleChecker.isAllowedToManageGrading(template.getRequest(), problem)) {
            template.addMainTab("Grading", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToGrading(problem.getId()));
        }

        if (problemRoleChecker.isAllowedToSubmit(template.getRequest(), problem)) {
            template.addMainTab("Submissions", org.iatoki.judgels.sandalphon.problem.programming.routes.ProgrammingProblemController.jumpToSubmissions(problem.getId()));
        }

        if (problemRoleChecker.isAuthorOrAbove(template.getRequest(), problem)) {
            template.addMainTab("Partners", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));
        }

        template.addMainTab("Versions", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));
    }

    public void appendBundleTabs(HtmlTemplate template, Problem problem) {
        template.addMainTab("Statements", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        if (problemRoleChecker.isAllowedToManageItems(template.getRequest(), problem)) {
            template.addMainTab("Items", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToItems(problem.getId()));
        }

        if (problemRoleChecker.isAllowedToSubmit(template.getRequest(), problem)) {
            template.addMainTab("Submissions", org.iatoki.judgels.sandalphon.problem.bundle.routes.BundleProblemController.jumpToSubmissions(problem.getId()));
        }

        if (problemRoleChecker.isAuthorOrAbove(template.getRequest(), problem)) {
            template.addMainTab("Partners", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));
        }

        template.addMainTab("Versions", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToVersions(problem.getId()));
    }

    protected Result renderStatementTemplate(HtmlTemplate template, Problem problem) {
        template.markBreadcrumbLocation("Statements", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToStatement(problem.getId()));

        template.addSecondaryTab("View", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problem.getId()));
        if (problemRoleChecker.isAllowedToUpdateStatement(template.getRequest(), problem)) {
            template.addSecondaryTab("Update", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.editStatement(problem.getId()));
        }

        template.addSecondaryTab("Media", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.listStatementMediaFiles(problem.getId()));

        if (problemRoleChecker.isAllowedToManageStatementLanguages(template.getRequest(), problem)) {
            template.addSecondaryTab("Languages", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.listStatementLanguages(problem.getId()));
        }

        return this.renderTemplate(template, problem);
    }

    protected void appendStatementLanguageSelection(HtmlTemplate template, String currentLanguage, Set<String> allowedLanguages, Call target) {
        Http.Request req = template.getRequest();
        template.transformContent(c -> statementLanguageSelectionLayout.render(target.absoluteURL(req, req.secure()), allowedLanguages, currentLanguage, c));
    }

    protected Result renderPartnerTemplate(HtmlTemplate template, Problem problem) {
        template.markBreadcrumbLocation("Partners", org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.jumpToPartners(problem.getId()));

        return this.renderTemplate(template, problem);
    }
}
