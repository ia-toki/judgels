package org.iatoki.judgels.sandalphon.problem.bundle.statement;

import static judgels.service.ServiceUtils.checkFound;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import org.iatoki.judgels.play.actor.ActorChecker;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.SandalphonSessionUtils;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemService;
import org.iatoki.judgels.sandalphon.problem.bundle.statement.html.bundleStatementView;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

@Singleton
public final class BundleProblemStatementController extends AbstractProblemController {
    private final ActorChecker actorChecker;
    private final BundleItemService bundleItemService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemStatementController(
            ActorChecker actorChecker,
            BundleItemService bundleItemService,
            ProblemService problemService) {

        super(problemService);
        this.actorChecker = actorChecker;
        this.bundleItemService = bundleItemService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(Http.Request req, long problemId) {
        String actorJid = actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);

        if (!ProblemControllerUtils.isAllowedToViewStatement(problemService, problem, language)) {
            return notFound();
        }

        ProblemStatement statement;
        try {
            statement = problemService.getStatement(actorJid, problem.getJid(), language);
        } catch (IOException e) {
            statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(language))
                    .text(BundleProblemStatementUtils.getDefaultStatement(language))
                    .build();
        }

        boolean isAllowedToSubmitByPartner = ProgrammingProblemControllerUtils.isAllowedToSubmit(problemService, problem);
        boolean isClean = !problemService.userCloneExists(actorJid, problem.getJid());

        String reasonNotAllowedToSubmit = null;

        if (!isAllowedToSubmitByPartner) {
            reasonNotAllowedToSubmit = "You are not allowed to submit.";
        } else if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        List<BundleItem> bundleItemList;
        try {
            bundleItemList = bundleItemService.getBundleItemsInProblemWithClone(problem.getJid(), actorJid);
        } catch (IOException e) {
            return notFound();
        }

        ImmutableList.Builder<Html> htmlBuilder = ImmutableList.builder();
        for (BundleItem bundleItem : bundleItemList) {
            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(bundleItem.getType());
            try {
                htmlBuilder.add(adapter.renderViewHtml(bundleItem, bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, bundleItem.getJid(), language)));
            } catch (IOException e) {
                try {
                    language = problemService.getDefaultLanguage(actorJid, problem.getJid());
                    htmlBuilder.add(adapter.renderViewHtml(bundleItem, bundleItemService.getItemConfInProblemWithCloneByJid(problem.getJid(), actorJid, bundleItem.getJid(), language)));
                } catch (IOException e1) {
                    return notFound();
                }
            }
        }

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(bundleStatementView.render(org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.postSubmit(problemId).absoluteURL(request(), request().secure()), statement, htmlBuilder.build(), reasonNotAllowedToSubmit));

        Set<String> allowedLanguages;
        try {
            allowedLanguages = ProblemControllerUtils.getAllowedLanguagesToView(problemService, problem);
        } catch (IOException e) {
            return notFound();
        }
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));

        template.markBreadcrumbLocation("View statement", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
        template.setPageTitle("Problem - View statement");

        return renderStatementTemplate(template, problemService, problem)
                .addingToSession(req, SandalphonSessionUtils.newCurrentStatementLanguage(language));
    }
}
