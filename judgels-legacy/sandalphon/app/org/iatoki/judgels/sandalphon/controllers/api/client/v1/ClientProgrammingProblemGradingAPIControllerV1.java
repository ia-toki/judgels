package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import judgels.service.client.ClientChecker;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Singleton
public final class ClientProgrammingProblemGradingAPIControllerV1 extends AbstractJudgelsAPIController {

    private final ClientChecker clientChecker;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ClientProgrammingProblemGradingAPIControllerV1(ClientChecker clientChecker, ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.clientChecker = clientChecker;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result downloadGradingFiles(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        try {
            ByteArrayOutputStream os = programmingProblemService.getZippedGradingFilesStream(problemJid);
            response().setContentType("application/x-download");
            response().setHeader("Content-disposition", "attachment; filename=" + problemJid + ".zip");
            return ok(os.toByteArray()).as("application/zip");
        } catch (IOException e) {
            e.printStackTrace();
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }
}
