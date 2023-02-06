package org.iatoki.judgels.sandalphon.controllers.api.internal;

import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import org.iatoki.judgels.jophiel.controllers.Secured;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemRoleChecker;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public final class InternalProgrammingProblemGradingAPIController extends AbstractJudgelsAPIController {
    private final ProblemStore problemStore;
    private final ProblemRoleChecker problemRoleChecker;
    private final ProgrammingProblemStore programmingProblemStore;

    @Inject
    public InternalProgrammingProblemGradingAPIController(
            ObjectMapper mapper,
            ProblemStore problemStore,
            ProblemRoleChecker problemRoleChecker,
            ProgrammingProblemStore programmingProblemStore) {

        super(mapper);
        this.problemStore = problemStore;
        this.problemRoleChecker = problemRoleChecker;
        this.programmingProblemStore = programmingProblemStore;
    }

    @Transactional(readOnly = true)
    public Result downloadGradingTestDataFile(Http.Request req, long problemId, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String testDataUrl = programmingProblemStore.getGradingTestDataFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(testDataUrl);
    }

    @Transactional(readOnly = true)
    public Result downloadGradingHelperFile(Http.Request req, long problemId, String filename) {
        String actorJid = req.attrs().get(Security.USERNAME);
        Problem problem = checkFound(problemStore.findProblemById(problemId));
        checkAllowed(problemRoleChecker.isAllowedToManageGrading(req, problem));

        String helper = programmingProblemStore.getGradingHelperFileURL(actorJid, problem.getJid(), filename);

        return okAsDownload(helper);
    }
}
