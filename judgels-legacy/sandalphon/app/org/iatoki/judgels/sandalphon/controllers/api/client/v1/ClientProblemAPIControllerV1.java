package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import org.iatoki.judgels.play.api.JudgelsAPIForbiddenException;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.client.problem.ClientProblem;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.ClientProblemFindRequestV1;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.ProblemV1;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public final class ClientProblemAPIControllerV1 extends AbstractJudgelsAPIController {

    private final ClientService clientService;
    private final ProblemService problemService;

    @Inject
    public ClientProblemAPIControllerV1(ClientService clientService, ProblemService problemService) {
        this.clientService = clientService;
        this.problemService = problemService;
    }

    @Transactional(readOnly = true)
    public Result findClientProblem() {
        authenticateAsJudgelsAppClient(clientService);
        ClientProblemFindRequestV1 requestBody = parseRequestBody(ClientProblemFindRequestV1.class);

        if (!clientService.clientExistsByJid(requestBody.clientJid)) {
            throw new JudgelsAPIForbiddenException("Client not recognized");
        }

        if (!problemService.problemExistsByJid(requestBody.problemJid)) {
            throw new JudgelsAPIForbiddenException("Problem not found");
        }

        if (!clientService.isClientAuthorizedForProblem(requestBody.problemJid, requestBody.clientJid)) {
            throw new JudgelsAPIForbiddenException("Client not authorized to use problem");
        }

        ClientProblem clientProblem = clientService.findClientProblemByClientJidAndProblemJid(requestBody.clientJid, requestBody.problemJid);
        if (!clientProblem.getSecret().equals(requestBody.problemSecret)) {
            throw new JudgelsAPIForbiddenException("Wrong client problem credentials");
        }

        try {
            Problem problem = problemService.findProblemByJid(requestBody.problemJid);

            ProblemV1 responseBody = new ProblemV1();

            responseBody.jid = problem.getJid();
            responseBody.slug = problem.getSlug();
            responseBody.defaultLanguage = problemService.getDefaultLanguage(null, requestBody.problemJid);
            responseBody.titlesByLanguage = problemService.getTitlesByLanguage(null, requestBody.problemJid);

            return okAsJson(responseBody);
        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }
}
