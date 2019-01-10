package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import org.iatoki.judgels.play.api.JudgelsAPIForbiddenException;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.api.JudgelsAppClientAPIIdentity;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleAnswer;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BundleGradingResult;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.BundleProblemGradeRequestV1;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.BundleProblemGraderImpl;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class ClientBundleProblemGradingAPIControllerV1 extends AbstractJudgelsAPIController {

    private final BundleProblemGraderImpl bundleProblemGrader;
    private final ClientService clientService;
    private final ProblemService problemService;

    @Inject
    public ClientBundleProblemGradingAPIControllerV1(BundleProblemGraderImpl bundleProblemGrader, ClientService clientService, ProblemService problemService) {
        this.bundleProblemGrader = bundleProblemGrader;
        this.clientService = clientService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result grade(String problemJid) {
        JudgelsAppClientAPIIdentity identity = authenticateAsJudgelsAppClient(clientService);
        BundleProblemGradeRequestV1 requestBody = parseRequestBody(BundleProblemGradeRequestV1.class);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }
        if (!clientService.clientExistsByJid(identity.getClientJid())) {
            throw new JudgelsAPIForbiddenException("Client not exists");
        }
        if (!clientService.isClientAuthorizedForProblem(problemJid, identity.getClientJid())) {
            throw new JudgelsAPIForbiddenException("Client not authorized to grade problem");
        }

        Problem problem = problemService.findProblemByJid(problemJid);

        BundleAnswer answer = new BundleAnswer(requestBody.answers, requestBody.languageCode);

        try {
            BundleGradingResult result =  bundleProblemGrader.gradeBundleProblem(problem.getJid(), answer);
            return okAsJson(result);
        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }

}
