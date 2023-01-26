package org.iatoki.judgels.sandalphon.problem.bundle.statement;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.play.template.HtmlTemplate;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemAdapters;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemStore;
import org.iatoki.judgels.sandalphon.problem.bundle.statement.html.bundleStatementView;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.twirl.api.Html;

@Singleton
public final class BundleProblemStatementController extends AbstractProblemController {
    private final ObjectMapper mapper;
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final BundleItemStore bundleItemStore;

    @Inject
    public BundleProblemStatementController(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            BundleItemStore bundleItemStore) {

        super(problemStore, problemRoleChecker);
        this.mapper = mapper;
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.bundleItemStore = bundleItemStore;
    }

    @Transactional(readOnly = true)
    public Result viewStatement(Http.Request req, long problemId) {
        String actorJid = getUserJid(req);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        String language = getStatementLanguage(req, problem);
        checkAllowed(problemRoleChecker.isAllowedToViewStatement(req, problem, language));

        ProblemStatement statement = problemStore.getStatement(actorJid, problem.getJid(), language);

        boolean isAllowedToSubmitByPartner = problemRoleChecker.isAllowedToSubmit(req, problem);
        boolean isClean = !problemStore.userCloneExists(actorJid, problem.getJid());

        String reasonNotAllowedToSubmit = null;

        if (!isAllowedToSubmitByPartner) {
            reasonNotAllowedToSubmit = "You are not allowed to submit.";
        } else if (!isClean) {
            reasonNotAllowedToSubmit = "Submission not allowed if there are local changes.";
        }

        List<BundleItem> items = bundleItemStore.getBundleItemsInProblemWithClone(problem.getJid(), actorJid);

        ImmutableList.Builder<Html> htmlBuilder = ImmutableList.builder();
        for (BundleItem item : items) {
            BundleItemAdapter adapter = BundleItemAdapters.fromItemType(item.getType(), mapper);
            try {
                htmlBuilder.add(adapter.renderViewHtml(
                        item,
                        bundleItemStore.getItemConfInProblemWithCloneByJid(
                                problem.getJid(),
                                actorJid,
                                item.getJid(),
                                language)));
            } catch (RuntimeException e) {
                if (e.getCause() instanceof IOException) {
                    language = problemStore.getStatementDefaultLanguage(actorJid, problem.getJid());
                    htmlBuilder.add(adapter.renderViewHtml(
                            item,
                            bundleItemStore.getItemConfInProblemWithCloneByJid(
                                    problem.getJid(),
                                    actorJid,
                                    item.getJid(),
                                    language)));
                } else {
                    throw e;
                }
            }
        }

        HtmlTemplate template = getBaseHtmlTemplate(req);
        template.setContent(bundleStatementView.render(org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.postSubmit(problemId).url(), statement, htmlBuilder.build(), reasonNotAllowedToSubmit));

        Set<String> allowedLanguages = problemRoleChecker.getAllowedStatementLanguagesToView(req, problem);
        appendStatementLanguageSelection(template, language, allowedLanguages, org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.switchLanguage(problem.getId()));

        template.markBreadcrumbLocation("View statement", org.iatoki.judgels.sandalphon.problem.base.statement.routes.ProblemStatementController.viewStatement(problemId));
        template.setPageTitle("Problem - View statement");

        return renderStatementTemplate(template, problem)
                .addingToSession(req, newCurrentStatementLanguage(language));
    }
}
