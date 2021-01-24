package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalProgrammingProblemGradingAPIController extends AbstractJudgelsAPIController {
    private final ProblemService problemService;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public InternalProgrammingProblemGradingAPIController(
            ProblemService problemService,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemService programmingProblemService) {

        this.problemService = problemService;
        this.problemRoleChecker = problemRoleChecker;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result downloadGradingTestDataFile(Http.Request req, long problemId, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String testDataUrl = programmingProblemService.getGradingTestDataFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(testDataUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadGradingHelperFile(Http.Request req, long problemId, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);
        Problem problem = checkFound(problemService.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String helper = programmingProblemService.getGradingHelperFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(helper);
    }
}
