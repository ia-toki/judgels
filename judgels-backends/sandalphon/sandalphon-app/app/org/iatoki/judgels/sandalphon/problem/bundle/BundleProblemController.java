package org.iatoki.judgels.sandalphon.problem.bundle;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.sandalphon.problem.base.AbstractProblemController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.statement.BundleProblemStatementUtils;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;

@Singleton
public final class BundleProblemController extends AbstractProblemController {
    private final BundleProblemService bundleProblemService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemController(BundleProblemService bundleProblemService, ProblemService problemService) {
        super(problemService);
        this.bundleProblemService = bundleProblemService;
        this.problemService = problemService;
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

        Problem problem;
        try {
            problem = problemService.createProblem(ProblemType.BUNDLE, slug, additionalNote, languageCode);
            ProblemStatement statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                    .text(BundleProblemStatementUtils.getDefaultStatement(languageCode))
                    .build();

            problemService.updateStatement(null, problem.getJid(), languageCode, statement);
            bundleProblemService.initBundleProblem(problem.getJid());
        } catch (IOException e) {
            return internalServerError();
        }

        problemService.initRepository(actorJid, problem.getJid());

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
