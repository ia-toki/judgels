package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.controllers.api.object.v2.ProblemInfoV2;
import org.iatoki.judgels.sandalphon.controllers.api.object.v2.ProblemStatementV2;
import org.iatoki.judgels.sandalphon.controllers.api.object.v2.ProblemSubmissionConfigV2;
import org.iatoki.judgels.sandalphon.controllers.api.object.v2.ProblemWorksheetV2;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.base.ProblemType;
import org.iatoki.judgels.sandalphon.problem.base.statement.ProblemStatement;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.problem.programming.grading.LanguageRestriction;
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
    private final ClientService clientService;
    private final UserService userService;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;

    @Inject
    public ClientProblemAPIControllerV2(ClientService clientService, UserService userService, ProblemService problemService, ProgrammingProblemService programmingProblemService) {
        this.clientService = clientService;
        this.userService = userService;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
    }

    @Transactional(readOnly = true)
    public Result getProblem(String problemJid) {
        authenticateAsJudgelsAppClient(clientService);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        return okAsJson(getProblemInfo(problemJid));
    }

    @Transactional(readOnly = true)
    public Result getProblemSubmissionConfig(String problemJid) {
        authenticateAsJudgelsAppClient(clientService);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        ProblemSubmissionConfigV2 result = new ProblemSubmissionConfigV2();

        try {
            result.gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
        } catch (IOException e) {
            result.gradingEngine = GradingEngineRegistry.getInstance().getDefaultEngine();
        }

        try {
            result.gradingLanguageRestriction = programmingProblemService.getLanguageRestriction(null, problemJid);
        } catch (IOException e) {
            result.gradingLanguageRestriction = LanguageRestriction.defaultRestriction();
        }

        BlackBoxGradingConfig config;
        try {
            config = (BlackBoxGradingConfig) programmingProblemService.getGradingConfig(null, problemJid);
        } catch (IOException e) {
            config = (BlackBoxGradingConfig) GradingEngineRegistry.getInstance().getEngine(result.gradingEngine).createDefaultGradingConfig();
        }

        result.sourceKeys = config.getSourceFileFields();

        return okAsJson(result);
    }

    @Transactional(readOnly = true)
    public Result getProblemWorksheet(String problemJid) {
        authenticateAsJudgelsAppClient(clientService);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        ProblemSubmissionConfigV2 submissionConfig = new ProblemSubmissionConfigV2();

        try {
            submissionConfig.gradingEngine = programmingProblemService.getGradingEngine(null, problemJid);
        } catch (IOException e) {
            submissionConfig.gradingEngine = GradingEngineRegistry.getInstance().getDefaultEngine();
        }

        try {
            submissionConfig.gradingLanguageRestriction = programmingProblemService.getLanguageRestriction(null, problemJid);
        } catch (IOException e) {
            submissionConfig.gradingLanguageRestriction = LanguageRestriction.defaultRestriction();
        }

        BlackBoxGradingConfig config;
        try {
            config = (BlackBoxGradingConfig) programmingProblemService.getGradingConfig(null, problemJid);
        } catch (IOException e) {
            config = (BlackBoxGradingConfig) GradingEngineRegistry.getInstance().getEngine(submissionConfig.gradingEngine).createDefaultGradingConfig();
        }

        submissionConfig.sourceKeys = config.getSourceFileFields();

        ProblemWorksheetV2 result = new ProblemWorksheetV2();
        result.submissionConfig = submissionConfig;

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

            result.statement = new ProblemStatementV2();
            result.statement.name = statement.getTitle();
            result.statement.timeLimit = config.getTimeLimitInMilliseconds();
            result.statement.memoryLimit = config.getMemoryLimitInKilobytes();
            result.statement.text = statement.getText();

            return okAsJson(result);
        } catch (IOException e) {

            throw new JudgelsAPIInternalServerErrorException(e);
        }
    }

    @Transactional(readOnly = true)
    public Result findProblemsByJids() {
        authenticateAsJudgelsAppClient(clientService);

        JsonNode problemJids = request().body().asJson();

        Map<String, ProblemInfoV2> result = new HashMap<>();

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
        authenticateAsJudgelsAppClient(clientService);

        String userJid = DynamicForm.form().bindFromRequest().get("userJid");

        Map<String, String> result = new HashMap<>();

        JsonNode slugs = request().body().asJson();
        for (JsonNode slugNode : slugs) {
            String slug = slugNode.asText();
            if (!problemService.problemExistsBySlug(slug)) {
                continue;
            }
            Problem problem = problemService.findProblemBySlug(slug);
            if (problem.getType() == ProblemType.PROGRAMMING && isPartnerOrAbove(userJid, problem)) {
                result.put(slug, problem.getJid());
            }
        }

        return okAsJson(result);
    }

    private ProblemInfoV2 getProblemInfo(String problemJid) {
        try {
            Problem problem = problemService.findProblemByJid(problemJid);

            ProblemInfoV2 res = new ProblemInfoV2();

            res.slug = problem.getSlug();
            res.defaultLanguage = simplifyLanguageCode(problemService.getDefaultLanguage(null, problemJid));
            res.namesByLanguage = problemService.getTitlesByLanguage(null, problemJid).entrySet()
                    .stream()
                    .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue()));
            return res;
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
