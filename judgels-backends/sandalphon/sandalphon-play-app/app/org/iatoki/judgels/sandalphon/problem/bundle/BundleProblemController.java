package org.iatoki.judgels.sandalphon.problem.bundle;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.editorial.ProblemEditorialStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementStore;
import judgels.sandalphon.problem.bundle.BundleProblemStore;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class BundleProblemController extends AbstractProblemController {
    private final ProblemStore problemStore;
    private final ProblemStatementStore statementStore;
    private final BundleProblemStore bundleProblemStore;

    @Inject
    public BundleProblemController(
            ProblemStore problemStore,
            ProblemStatementStore statementStore,
            ProblemEditorialStore editorialStore,
            ProblemRoleChecker problemRoleChecker,
            BundleProblemStore bundleProblemStore) {

        super(problemStore, statementStore, editorialStore, problemRoleChecker);
        this.bundleProblemStore = bundleProblemStore;
        this.problemStore = problemStore;
        this.statementStore = statementStore;
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

        Problem problem = problemStore.createProblem(ProblemType.BUNDLE, slug, additionalNote);
        statementStore.initStatements(problem.getJid(), ProblemType.BUNDLE, languageCode);

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
