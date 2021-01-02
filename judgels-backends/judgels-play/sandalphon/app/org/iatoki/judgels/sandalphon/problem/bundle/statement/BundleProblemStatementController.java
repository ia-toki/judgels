package org.iatoki.judgels.sandalphon.problem.bundle.statement;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.AbstractBundleProblemController;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemService;
import org.iatoki.judgels.sandalphon.problem.bundle.statement.html.bundleStatementView;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import play.db.jpa.Transactional;
import play.mvc.Result;
import play.twirl.api.Html;

@Singleton
public final class BundleProblemStatementController extends AbstractBundleProblemController {

    private final BundleItemService bundleItemService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemStatementController(BundleItemService bundleItemService, ProblemService problemService) {
        this.bundleItemService = bundleItemService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(long problemId) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);
        try {
            ProblemControllerUtils.establishStatementLanguage(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }

        if (!ProblemControllerUtils.isAllowedToViewStatement(problemService, problem)) {
            return notFound();
        }

        ProblemStatement statement;
        try {
            statement = problemService.getStatement(IdentityUtils.getUserJid(), problem.getJid(), ProblemControllerUtils.getCurrentStatementLanguage());
        } catch (IOException e) {
            statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(ProblemControllerUtils.getCurrentStatementLanguage()))
                    .text(BundleProblemStatementUtils.getDefaultStatement(ProblemControllerUtils.getCurrentStatementLanguage()))
                    .build();
        }

        boolean isAllowedToSubmitByPartner = ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem);
        boolean isClean = !problemService.userCloneExists(IdentityUtils.getUserJid(), problem.getJid());

        String reasonNotAllowedToSubmit = null;

        if (!isAllowedToSubmitByPartner) {
            reasonNotAllowedToSubmit = "You are not allowed to submit.";
        } else if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        List<BundleItem> bundleItemList;
        try {
            bundleItemList = bundleItemService.getBundleItemsInProblemWithClone(problem.getJid(), IdentityUtils.getUserJid());
        } catch (IOException e) {
            e.printStackTrace();
            return notFound();
        }

        ImmutableList.Builder<Html> htmlBuilder = ImmutableList.builder();
        for (BundleItem bundleItem : bundleItemList) {
            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(bundleItem.getType());
            try {
                htmlBuilder.add(adapter.renderViewHtml(bundleItem, bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), bundleItem.getJid(), ProblemControllerUtils.getCurrentStatementLanguage())));
            } catch (IOException e) {
                try {
                    ProblemControllerUtils.setCurrentStatementLanguage(ProblemControllerUtils.getDefaultStatementLanguage(problemService, problem));
                    htmlBuilder.add(adapter.renderViewHtml(bundleItem, bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), IdentityUtils.getUserJid(), bundleItem.getJid(), ProblemControllerUtils.getCurrentStatementLanguage())));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return notFound();
                }
            }
        }

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(bundleStatementView.render(org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.postSubmit(problemId).absoluteURL(request(), request().secure()), statement, htmlBuilder.build(), reasonNotAllowedToSubmit));

        Set<String> allowedLanguages;
        try {
            allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToView(problemService, problem);
        } catch (IOException e) {
            e.printStackTrace();
            return notFound();
        }
        appendStatementLanguageSelection(template, ProblemControllerUtils.getCurrentStatementLanguage(), allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));

        template.markBreadcrumbLocation("View statement", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
        template.setPageTitle("Problem - View statement");

        return renderStatementTemplate(template, problemService, problem);
    }
}
