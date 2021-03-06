package org.iatoki.judgels.sandalphon.controllers.api.client.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.gabriel.api.GradingConfig;
import judgels.jophiel.api.client.user.ClientUserService;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.programming.ProblemLimits;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.problem.bundle.ItemProcessorRegistry;
import org.iatoki.judgels.play.controllers.apis.AbstractJudgelsAPIController;
import org.iatoki.judgels.sandalphon.StatementLanguageStatus;
import org.iatoki.judgels.sandalphon.problem.base.ProblemStore;
import org.iatoki.judgels.sandalphon.problem.bundle.item.BundleItemStore;
import org.iatoki.judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import play.db.jpa.Transactional;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;

@Singleton
@Security.Authenticated(ClientSecured.class)
public final class ClientProblemAPIControllerV2 extends AbstractJudgelsAPIController {
    private final ObjectMapper mapper;
    private final ClientUserService userService;
    private final ProblemStore problemStore;
    private final ProgrammingProblemStore programmingProblemStore;
    private final BundleItemStore bundleItemStore;
    private final ItemProcessorRegistry itemProcessorRegistry;

    @Inject
    public ClientProblemAPIControllerV2(
            ObjectMapper mapper,
            ClientUserService userService,
            ProblemStore problemStore,
            ProgrammingProblemStore programmingProblemStore,
            BundleItemStore bundleItemStore,
            ItemProcessorRegistry itemProcessorRegistry) {

        super(mapper);
        this.mapper = mapper;
        this.userService = userService;
        this.problemStore = problemStore;
        this.programmingProblemStore = programmingProblemStore;
        this.bundleItemStore = bundleItemStore;
        this.itemProcessorRegistry = itemProcessorRegistry;
    }

    @Transactional(readOnly = true)
    public Result getProblem(Http.Request req, String problemJid) {
        if (!problemStore.problemExistsByJid(problemJid)) {
            return Results.notFound();
        }

        return okAsJson(req, getProblemInfo(problemJid));
    }

    @Transactional(readOnly = true)
    public Result getProblemSubmissionConfig(Http.Request req, String problemJid) {
        if (!problemStore.problemExistsByJid(problemJid)) {
            return Results.notFound();
        }

        return okAsJson(req, getSubmissionConfig(problemJid));
    }

    @Transactional(readOnly = true)
    public Result getProgrammingProblemWorksheet(Http.Request req, String problemJid) {
        if (!problemStore.problemExistsByJid(problemJid)) {
            return Results.notFound();
        }

        Problem problem = problemStore.findProblemByJid(problemJid);
        if (ProblemType.valueOf(problem.getType().name()) != ProblemType.PROGRAMMING) {
            return notFound();
        }

        GradingConfig config = programmingProblemStore.getGradingConfig(null, problemJid);
        String language = sanitizeLanguageCode(problemJid, req.getQueryString("language"));

        return okAsJson(req, new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                .statement(problemStore.getStatement(null, problemJid, language))
                .limits(new ProblemLimits.Builder()
                        .timeLimit(config.getTimeLimit())
                        .memoryLimit(config.getMemoryLimit())
                        .build())
                .submissionConfig(getSubmissionConfig(problemJid))
                .build());
    }

    @Transactional(readOnly = true)
    public Result getBundleProblemWorksheet(Http.Request req, String problemJid) throws IOException {
        if (!problemStore.problemExistsByJid(problemJid)) {
            return notFound();
        }

        Problem problem = problemStore.findProblemByJid(problemJid);
        if (ProblemType.valueOf(problem.getType().name()) != ProblemType.BUNDLE) {
            return notFound();
        }

        String language = sanitizeLanguageCode(problemJid, req.getQueryString("language"));

        List<BundleItem> items = bundleItemStore.getBundleItemsInProblemWithClone(problemJid, null);
        List<Item> itemsWithConfig = new ArrayList<>();
        for (BundleItem item : items) {
            String itemConfigString = bundleItemStore.getItemConfInProblemWithCloneByJid(
                    problemJid, null, item.getJid(), language);

            Item itemWithConfig = new Item.Builder()
                    .jid(item.getJid())
                    .type(item.getType())
                    .number(item.getNumber())
                    .meta(item.getMeta())
                    .config(itemProcessorRegistry.get(item.getType()).parseItemConfigFromString(mapper, itemConfigString))
                    .build();
            itemsWithConfig.add(itemWithConfig);
        }

        return okAsJson(req, new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                        .statement(problemStore.getStatement(null, problemJid, language))
                        .items(itemsWithConfig)
                        .build());
    }

    @Transactional(readOnly = true)
    public Result findProblemsByJids(Http.Request req) {
        JsonNode problemJids = req.body().asJson();

        Map<String, ProblemInfo> result = new HashMap<>();

        for (JsonNode problemJidNode : problemJids) {
            String problemJid = problemJidNode.asText();
            if (problemStore.problemExistsByJid(problemJid)) {
                result.put(problemJid, getProblemInfo(problemJid));
            }
        }
        return okAsJson(req, result);
    }

    @Transactional(readOnly = true)
    public Result translateAllowedSlugToJids(Http.Request req) {
        String userJid = req.getQueryString("userJid");

        Map<String, String> result = new HashMap<>();

        JsonNode slugs = req.body().asJson();
        for (JsonNode slugNode : slugs) {
            String slug = slugNode.asText();
            if (!problemStore.problemExistsBySlug(slug)) {
                continue;
            }
            Problem problem = problemStore.findProblemBySlug(slug);
            if (isPartnerOrAbove(userJid, problem)) {
                result.put(slug, problem.getJid());
            }
        }

        return okAsJson(req, result);
    }

    private ProblemInfo getProblemInfo(String problemJid) {
        Problem problem = problemStore.findProblemByJid(problemJid);

        return new ProblemInfo.Builder()
                .slug(problem.getSlug())
                .type(ProblemType.valueOf(problem.getType().name()))
                .defaultLanguage(simplifyLanguageCode(problemStore.getDefaultLanguage(null, problemJid)))
                .titlesByLanguage(problemStore.getTitlesByLanguage(null, problemJid).entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    private boolean isPartnerOrAbove(String userJid, Problem problem) {
        return problem.getAuthorJid().equals(userJid)
            || problemStore.isUserPartnerForProblem(problem.getJid(), userJid)
            || userService.getUserRole(userJid).getSandalphon().orElse("").equals("ADMIN");
    }

    private ProblemSubmissionConfig getSubmissionConfig(String problemJid) {
        return new ProblemSubmissionConfig.Builder()
                .gradingEngine(programmingProblemStore.getGradingEngine(null, problemJid))
                .gradingLanguageRestriction(programmingProblemStore.getLanguageRestriction(null, problemJid))
                .sourceKeys(programmingProblemStore.getGradingConfig(null, problemJid).getSourceFileFields())
                .build();
    }

    private String sanitizeLanguageCode(String problemJid, String language) {
        Map<String, StatementLanguageStatus> availableLanguages = problemStore.getAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language;
        if (!simplifiedLanguages.containsKey(language) || availableLanguages.get(simplifiedLanguages.get(language)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(problemStore.getDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }

    private static String simplifyLanguageCode(String code) {
        if (code.startsWith("zh")) {
            return code;
        }
        String[] tokens = code.split("-");
        if (tokens.length < 2) {
            return code;
        }
        return tokens[0];
    }
}
