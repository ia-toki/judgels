package org.iatoki.judgels.sandalphon.controllers.api.client.v1;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.service.api.client.Client;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.play.api.JudgelsAPIForbiddenException;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestrictionAdapter;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.programming.grading.GradingEngineAdapterRegistry;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.controllers.api.object.v1.ProgrammingProblemStatementRenderRequestV1;
import org.iatoki.judgels.sandalphon.controllers.api.util.TOTPUtils;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.statement.html.statementLanguageSelectionLayout;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import play.db.jpa.Transactional;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public final class ClientProgrammingProblemStatementAPIControllerV1 extends AbstractJudgelsAPIController {

    private final ClientService clientService;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ClientProgrammingProblemStatementAPIControllerV1(ClientService clientService, ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.clientService = clientService;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result renderStatement(String problemJid) {
        ProgrammingProblemStatementRenderRequestV1 requestBody = parseRequestBodyAsUrlFormEncoded(ProgrammingProblemStatementRenderRequestV1.class);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }
        if (!clientService.clientExistsByJid(requestBody.clientJid)) {
            throw new JudgelsAPIForbiddenException("Client not exists");
        }

        Client client = clientService.findClientByJid(requestBody.clientJid);
        if (!TOTPUtils.match(client.getSecret(), requestBody.totpCode)) {
            throw new JudgelsAPIForbiddenException("TOTP code mismatch");
        }

        try {
            Map<String, StatementLanguageStatus> availableStatementLanguages = problemService.getAvailableLanguages(null, problemJid);

            String statementLanguage = requestBody.statementLanguage;
            if (!availableStatementLanguages.containsKey(statementLanguage) || availableStatementLanguages.get(statementLanguage) == StatementLanguageStatus.DISABLED) {
                statementLanguage = problemService.getDefaultLanguage(null, problemJid);
            }

            ProblemStatement statement = problemService.getStatement(null, problemJid, statementLanguage);

            Set<String> allowedStatementLanguages = availableStatementLanguages.entrySet().stream().filter(e -> e.getValue() == StatementLanguageStatus.ENABLED).map(e -> e.getKey()).collect(Collectors.toSet());

            Set<String> allowedGradingLanguages;
            if (requestBody.allowedGradingLanguages.isEmpty()) {
                allowedGradingLanguages = ImmutableSet.of();
            } else {
                allowedGradingLanguages = ImmutableSet.copyOf(requestBody.allowedGradingLanguages.split(","));
            }

            String gradingEngine;
            try {
                gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
            } catch (IOException e) {
                gradingEngine = GradingEngineRegistry.getInstance().getDefaultEngine();
            }

            LanguageRestriction problemLanguageRestriction;
            try {
                problemLanguageRestriction = programmingProblemService.getLanguageRestriction(null, problemJid);
            } catch (IOException e) {
                problemLanguageRestriction = LanguageRestriction.noRestriction();
            }

            LanguageRestriction clientLanguageRestriction = LanguageRestriction.of(allowedGradingLanguages);

            Set<String> finalAllowedGradingLanguages = LanguageRestrictionAdapter.getFinalAllowedLanguageNames(ImmutableList.of(problemLanguageRestriction, clientLanguageRestriction));

            GradingConfig config;
            try {
                config = programmingProblemService.getGradingConfig(null, problemJid);
            } catch (IOException e) {
                config = GradingEngineRegistry.getInstance().getEngine(gradingEngine).createDefaultGradingConfig();
            }

            Html statementHtml = GradingEngineAdapterRegistry.getInstance().getByGradingEngineName(gradingEngine).renderViewStatement(requestBody.postSubmitUrl, statement, config, gradingEngine, finalAllowedGradingLanguages, requestBody.reasonNotAllowedToSubmit);
            if (requestBody.switchStatementLanguageUrl != null) {
                statementHtml = statementLanguageSelectionLayout.render(requestBody.switchStatementLanguageUrl, allowedStatementLanguages, statementLanguage, statementHtml);
            }

            return ok(statementHtml);

        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }
}
