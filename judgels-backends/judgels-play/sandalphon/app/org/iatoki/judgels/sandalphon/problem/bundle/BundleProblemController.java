package org.iatoki.judgels.sandalphon.problem.bundle;

import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatementUtils;
import org.iatoki.judgels.sandalphon.problem.bundle.statement.BundleProblemStatementUtils;
import play.db.jpa.Transactional;
import play.mvc.Result;

@Singleton
public final class BundleProblemController extends AbstractBundleProblemController {
    private final BundleProblemService bundleProblemService;
    private final ProblemService problemService;

    @Inject
    public BundleProblemController(BundleProblemService bundleProblemService, ProblemService problemService) {
        this.bundleProblemService = bundleProblemService;
        this.problemService = problemService;
    }

    @Transactional
    public Result createBundleProblem() {
        if (!ProblemControllerUtils.wasProblemJustCreated()) {
            return badRequest();
        }

        String slug = ProblemControllerUtils.getJustCreatedProblemSlug();
        String additionalNote = ProblemControllerUtils.getJustCreatedProblemAdditionalNote();
        String languageCode = ProblemControllerUtils.getJustCreatedProblemInitLanguageCode();

        Problem problem;
        try {
            problem = problemService.createProblem(ProblemType.BUNDLE, slug, additionalNote, languageCode, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());
            ProblemStatement statement = new ProblemStatement.Builder()
                    .title(ProblemStatementUtils.getDefaultTitle(languageCode))
                    .text(BundleProblemStatementUtils.getDefaultStatement(languageCode))
                    .build();

            problemService.updateStatement(null, problem.getJid(), languageCode, statement);
            bundleProblemService.initBundleProblem(problem.getJid());
        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError();
        }

        problemService.initRepository(IdentityUtils.getUserJid(), problem.getJid());

        ProblemControllerUtils.setCurrentStatementLanguage(ProblemControllerUtils.getJustCreatedProblemInitLanguageCode());
        ProblemControllerUtils.removeJustCreatedProblem();

        return redirect(org.iatoki.judgels.sandalphon.problem.base.routes.ProblemController.enterProblem(problem.getId()));
    }

    public Result jumpToItems(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.bundle.item.routes.BundleItemController.viewItems(problemId));
    }

    public Result jumpToSubmissions(long problemId) {
        return redirect(org.iatoki.judgels.sandalphon.problem.bundle.submission.routes.BundleProblemSubmissionController.viewSubmissions(problemId));
    }
}
