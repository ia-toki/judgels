package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemLimits;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import judgels.service.client.ClientChecker;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.user.UserService;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ClientProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ClientChecker clientChecker;
    private final UserService userService;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ClientProblemAPIControllerV2(ClientChecker clientChecker, UserService userService, ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.clientChecker = clientChecker;
        this.userService = userService;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result getProblem(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        return okAsJson(getProblemInfo(problemJid));
    }

    @Transactional(readOnly = true)
    public Result getProblemSubmissionConfig(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        ProblemSubmissionConfig.Builder result = new ProblemSubmissionConfig.Builder();

        String gradingEngine;
        try {
            gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
        } catch (IOException e) {
            gradingEngine = GradingEngineRegistry.getInstance().getDefaultEngine();
        }
        result.gradingEngine(gradingEngine);

        try {
            result.gradingLanguageRestriction(programmingProblemService.getLanguageRestriction(null, problemJid));
        } catch (IOException e) {
            result.gradingLanguageRestriction(LanguageRestriction.noRestriction());
        }

        BlackBoxGradingConfig config;
        try {
            config = (BlackBoxGradingConfig) programmingProblemService.getGradingConfig(null, problemJid);
        } catch (IOException e) {
            config = (BlackBoxGradingConfig) GradingEngineRegistry.getInstance().getEngine(gradingEngine).createDefaultGradingConfig();
        }

        result.sourceKeys(config.getSourceFileFields());

        return okAsJson(result);
    }

    @Transactional(readOnly = true)
    public Result getProblemWorksheet(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        ProblemSubmissionConfig.Builder submissionConfig = new ProblemSubmissionConfig.Builder();

        String gradingEngine;
        try {
            gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
        } catch (IOException e) {
            gradingEngine = GradingEngineRegistry.getInstance().getDefaultEngine();
        }
        submissionConfig.gradingEngine(gradingEngine);

        try {
            submissionConfig.gradingLanguageRestriction(programmingProblemService.getLanguageRestriction(null, problemJid));
        } catch (IOException e) {
            submissionConfig.gradingLanguageRestriction(LanguageRestriction.noRestriction());
        }

        BlackBoxGradingConfig config;
        try {
            config = (BlackBoxGradingConfig) programmingProblemService.getGradingConfig(null, problemJid);
        } catch (IOException e) {
            config = (BlackBoxGradingConfig) GradingEngineRegistry.getInstance().getEngine(gradingEngine).createDefaultGradingConfig();
        }

        submissionConfig.sourceKeys(config.getSourceFileFields());

        ProblemWorksheet.Builder result = new ProblemWorksheet.Builder();
        result.submissionConfig(submissionConfig.build());

        String language = DynamicForm.form().bindFromRequest().get("language");

        try {
            Map<String, StatementLanguageStatus> availableLanguages = problemService.getAvailableLanguages(null, problemJid);
            Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

            if (!simplifiedLanguages.containsKey(language) || availableLanguages.get(simplifiedLanguages.get(language)) == StatementLanguageStatus.DISABLED) {
                language = simplifyLanguageCode(problemService.getDefaultLanguage(null, problemJid));
            }

            ProblemStatement statement = problemService.getStatement(null, problemJid, simplifiedLanguages.get(language));
            result.statement(statement);

            ProblemLimits limits = new ProblemLimits.Builder()
                    .timeLimit(config.getTimeLimitInMilliseconds())
                    .memoryLimit(config.getMemoryLimitInKilobytes())
                    .build();
            result.limits(limits);

            return okAsJson(result.build());
        } catch (IOException e) {

            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }

    @Transactional(readOnly = true)
    public Result findProblemsByJids() {
        authenticateAsJudgelsAppClient(clientChecker);

        JsonNode problemJids = request().body().asJson();

        Map<String, ProblemInfo> result = new HashMap<>();

        for (JsonNode problemJidNode : problemJids) {
            String problemJid = problemJidNode.asText();
            if (problemService.problemExistsByJid(problemJid)) {
                result.put(problemJid, getProblemInfo(problemJid));
            }
        }
        return okAsJson(result);
    }

    @Transactional(readOnly = true)
    public Result translateAllowedSlugToJids() {
        authenticateAsJudgelsAppClient(clientChecker);

        String userJid = DynamicForm.form().bindFromRequest().get("userJid");

        Map<String, String> result = new HashMap<>();

        JsonNode slugs = request().body().asJson();
        for (JsonNode slugNode : slugs) {
            String slug = slugNode.asText();
            if (!problemService.problemExistsBySlug(slug)) {
                continue;
            }
            Problem problem = problemService.findProblemBySlug(slug);
            if (isPartnerOrAbove(userJid, problem)) {
                result.put(slug, problem.getJid());
            }
        }

        return okAsJson(result);
    }

    private ProblemInfo getProblemInfo(String problemJid) {
        try {
            Problem problem = problemService.findProblemByJid(problemJid);

            ProblemInfo.Builder res = new ProblemInfo.Builder();

            res.slug(problem.getSlug());
            res.defaultLanguage(simplifyLanguageCode(problemService.getDefaultLanguage(null, problemJid)));
            res.titlesByLanguage(problemService.getTitlesByLanguage(null, problemJid).entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())));
            return res.build();
        } catch (IOException e) {
            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }

    private boolean isPartnerOrAbove(String userJid, Problem problem) {
        return problem.getAuthorJid().equals(userJid)
            || problemService.isUserPartnerForProblem(problem.getJid(), userJid)
            || userService.findUserByJid(userJid).getRoles().contains("admin");
    }

    private static String simplifyLanguageCode(String code) {
        String[] tokens = code.split("-");
        if (tokens.length < 2) {
            return code;
        }
        return tokens[0];
    }
}
