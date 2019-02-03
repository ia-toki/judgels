package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import judgels.service.client.ClientChecker;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.ProgrammingProblemInfoV1;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class ClientProgrammingProblemAPIControllerV1 extends AbstractJudgelsAPIController {

    private final ClientChecker clientChecker;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ClientProgrammingProblemAPIControllerV1(ClientChecker clientChecker, ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.clientChecker = clientChecker;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result getProgrammingProblemInfo(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        try {
            ProgrammingProblemInfoV1 responseBody = new ProgrammingProblemInfoV1();

            responseBody.gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
            responseBody.gradingLastUpdateTime = programmingProblemService.getGradingLastUpdateTime(null, problemJid).getTime();

            return okAsJson(responseBody);
        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }
}
