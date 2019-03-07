package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.problem.programming.ProblemLimits;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.problem.bundle.ItemProcessorRegistry;
import judgels.service.client.ClientChecker;
import org.iatoki.judgels.gabriel.GradingEngineRegistry;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingConfig;
import org.iatoki.judgels.play.api.JudgelsAPIInternalServerErrorException;
import org.iatoki.judgels.play.api.JudgelsAPINotFoundException;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.Problem;
import org.iatoki.judgels.sandalphon.problem.base.ProblemService;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItem;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemService;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemService;
import org.iatoki.judgels.sandalphon.user.UserService;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public final class ClientProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new Jdk8Module());

    private final ClientChecker clientChecker;
    private final UserService userService;
    private final ProblemService problemService;
    private final ProgrammingProblemService programmingProblemService;
    private final BundleItemService bundleItemService;
    private final ItemProcessorRegistry itemProcessorRegistry;

    @Inject
    public ClientProblemAPIControllerV2(
            ClientChecker clientChecker,
            UserService userService,
            ProblemService problemService,
            ProgrammingProblemService programmingProblemService,
            BundleItemService bundleItemService,
            ItemProcessorRegistry itemProcessorRegistry) {
        this.clientChecker = clientChecker;
        this.userService = userService;
        this.problemService = problemService;
        this.programmingProblemService = programmingProblemService;
        this.bundleItemService = bundleItemService;
        this.itemProcessorRegistry = itemProcessorRegistry;
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

        return okAsJson(getSubmissionConfig(problemJid));
    }

    @Transactional(readOnly = true)
    public Result getProgrammingProblemWorksheet(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);

        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        Problem problem = problemService.findProblemByJid(problemJid);
        if (ProblemType.valueOf(problem.getType().name()) != ProblemType.PROGRAMMING) {
            throw new JudgelsAPINotFoundException();
        }

        judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder result =
                new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder();

        ProblemSubmissionConfig submissionConfig = getSubmissionConfig(problemJid);
        result.submissionConfig(submissionConfig);

        BlackBoxGradingConfig config = getBlackBoxGradingConfig(problemJid, submissionConfig.getGradingEngine());

        try {
            String language = sanitizeLanguageCode(problemJid, DynamicForm.form().bindFromRequest().get("language"));

            ProblemStatement statement = problemService.getStatement(null, problemJid, language);
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
    public Result getBundleProblemWorksheet(String problemJid) {
        authenticateAsJudgelsAppClient(clientChecker);
        if (!problemService.problemExistsByJid(problemJid)) {
            throw new JudgelsAPINotFoundException();
        }

        Problem problem = problemService.findProblemByJid(problemJid);
        if (ProblemType.valueOf(problem.getType().name()) != ProblemType.BUNDLE) {
            throw new JudgelsAPINotFoundException();
        }

        try {
            String language = sanitizeLanguageCode(problemJid, DynamicForm.form().bindFromRequest().get("language"));

            ProblemStatement statement = problemService.getStatement(null, problemJid, language);

            List<BundleItem> items = bundleItemService.getBundleItemsInProblemWithClone(problemJid, null);
            List<Item> itemsWithConfig = new ArrayList<>();
            for (BundleItem item : items) {
                String itemConfigString = bundleItemService.getItemConfInProblemWithCloneByJid(
                        problemJid, null, item.getJid(), language);
                ItemType type = ItemType.valueOf(item.getType().name());

                Item itemWithConfig = new Item.Builder()
                        .jid(item.getJid())
                        .type(type)
                        .meta(item.getMeta())
                        .config(itemProcessorRegistry.get(type).parseItemConfigFromString(MAPPER, itemConfigString))
                        .build();
                itemsWithConfig.add(itemWithConfig);
            }

            return okAsJson(
                    new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                            .statement(statement)
                            .items(itemsWithConfig)
                            .build()
            );
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
            res.type(ProblemType.valueOf(problem.getType().name()));
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

    private String getGradingEngine(String problemJid) {
        try {
            return programmingProblemService.getGradingEngine(null, problemJid);
        } catch (IOException e) {
            return GradingEngineRegistry.getInstance().getDefaultEngine();
        }
    }

    private LanguageRestriction getLanguageRestriction(String problemJid) {
        try {
            return programmingProblemService.getLanguageRestriction(null, problemJid);
        } catch (IOException e) {
            return LanguageRestriction.noRestriction();
        }
    }

    private BlackBoxGradingConfig getBlackBoxGradingConfig(String problemJid, String gradingEngine) {
        try {
            return (BlackBoxGradingConfig) programmingProblemService.getGradingConfig(null, problemJid);
        } catch (IOException e) {
            return (BlackBoxGradingConfig) GradingEngineRegistry.getInstance()
                    .getEngine(gradingEngine)
                    .createDefaultGradingConfig();
        }
    }

    private ProblemSubmissionConfig getSubmissionConfig(String problemJid) {
        ProblemSubmissionConfig.Builder submissionConfig = new ProblemSubmissionConfig.Builder();

        String gradingEngine = getGradingEngine(problemJid);
        submissionConfig.gradingEngine(gradingEngine);

        LanguageRestriction languageRestriction = getLanguageRestriction(problemJid);
        submissionConfig.gradingLanguageRestriction(languageRestriction);

        BlackBoxGradingConfig config = getBlackBoxGradingConfig(problemJid, gradingEngine);
        submissionConfig.sourceKeys(config.getSourceFileFields());

        return submissionConfig.build();
    }

    private String sanitizeLanguageCode(String problemJid, String language) throws IOException {
        Map<String, StatementLanguageStatus> availableLanguages = problemService.getAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        if (!simplifiedLanguages.containsKey(language) || availableLanguages.get(simplifiedLanguages.get(language)) == StatementLanguageStatus.DISABLED) {
            language = simplifyLanguageCode(problemService.getDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(language);
    }

    private static String simplifyLanguageCode(String code) {
        String[] tokens = code.split("-");
        if (tokens.length < 2) {
            return code;
        }
        return tokens[0];
    }
}
