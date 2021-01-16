package org.iatoki.judgels.sandalphon.controllers.api.internal;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemNotFoundException;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalProgrammingProblemGradingAPIController extends AbstractJudgelsAPIController {

    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public InternalProgrammingProblemGradingAPIController(ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result downloadGradingTestDataFile(long problemId, String filename) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return Results.notFound();
        }

        String testDataUrl = programmingProblemService.getGradingTestDataFileURL(IdentityUtils.getUserJid(), problem.getJid(), filename);

        return okAsDownload(testDataUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadGradingHelperFile(long problemId, String filename) throws ProblemNotFoundException {
        Problem problem = problemService.findProblemById(problemId);

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return Results.notFound();
        }

        String helper = programmingProblemService.getGradingHelperFileURL(IdentityUtils.getUserJid(), problem.getJid(), filename);

        return okAsDownload(helper);
    }
}
