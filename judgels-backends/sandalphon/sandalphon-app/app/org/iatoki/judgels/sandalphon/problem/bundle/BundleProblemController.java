package org.iatoki.judgels.sandalphon.problem.bundle;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import judgels.sandalphon.problem.bundle.BundleProblemStore;
import judgels.sandalphon.problem.bundle.statement.BundleProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class BundleProblemController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final BundleProblemStore bundleProblemStore;

    @Inject
    public BundleProblemController(
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            BundleProblemStore bundleProblemStore) {

        super(problemStore, problemRoleChecker);
        this.bundleProblemStore = bundleProblemStore;
        this.problemStore = problemStore;
    }

    @Transactional
    public Result createBundleProblem(Http.Request req) {
        String actorJid = getUserJid(req);

        if (!wasProblemJustCreated(req)) {
            return badRequest();
        }

        String slug = getJustCreatedProblemSlug(req);
        String additionalNote = getJustCreatedProblemAdditionalNote(req);
        String languageCode = getJustCreatedProblemInitLanguage(req);

        Problem problem = problemStore.createProblem(ProblemType.BUNDLE, slug, additionalNote, languageCode);
        ProblemStatement statement = new ProblemStatement.Builder()
                .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                .text(BundleProblemStatementUtils.getDefaultStatement(languageCode))
                .build();

        problemStore.updateStatement(null, problem.getJid(), languageCode, statement);
        bundleProblemStore.initBundleProblem(problem.getJid());

        problemStore.initRepository(actorJid, problem.getJid());

        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.enterProblem(problem.getId()))
                .addingToSession(req, newCurrentStatementLanguage(getJustCreatedProblemInitLanguage(req)))
                .removingFromSession(req, removeJustCreatedProblem());
    }

    public Result jumpToItems(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.bundle.item.routes.BundleItemController.viewItems(problemId));
    }

    public Result jumpToSubmissions(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.viewSubmissions(problemId));
    }
}
