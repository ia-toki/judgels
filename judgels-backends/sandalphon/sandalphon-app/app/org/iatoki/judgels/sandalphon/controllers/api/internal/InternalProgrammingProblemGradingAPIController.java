package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkFound;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.actor.ActorChecker;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemControllerUtils;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalProgrammingProblemGradingAPIController extends AbstractJudgelsAPIController {
    private final ActorChecker actorChecker;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public InternalProgrammingProblemGradingAPIController(
            ActorChecker actorChecker,
            ProblemService problemService,
            ProgrammingProblemService programmingProblemService) {

        this.actorChecker = actorChecker;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result downloadGradingTestDataFile(Http.Request req, long problemId, String filename) {
        String actorJid = actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return Results.notFound();
        }

        String testDataUrl = programmingProblemService.getGradingTestDataFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(testDataUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadGradingHelperFile(Http.Request req, long problemId, String filename) {
        String actorJid = actorChecker.check(req);

        Problem problem = checkFound(problemService.findProblemById(problemId));

        if (!ProgrammingProblemControllerUtils.isAllowedToManageGrading(problemService, problem)) {
            return Results.notFound();
        }

        String helper = programmingProblemService.getGradingHelperFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(helper);
    }
}
