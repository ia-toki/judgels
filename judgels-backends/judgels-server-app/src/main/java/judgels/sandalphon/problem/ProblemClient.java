package judgels.sandalphon.problem;

import static java.util.stream.Collectors.toMap;
import static judgels.sandalphon.resource.LanguageUtils.simplifyLanguageCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.JudgelsAppConfiguration;
import judgels.gabriel.api.GradingConfig;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.problem.Problem;
import judgels.sandalphon.api.problem.ProblemEditorial;
import judgels.sandalphon.api.problem.ProblemEditorialInfo;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemMetadata;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemType;
import judgels.sandalphon.api.problem.bundle.BundleItem;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.bundle.ItemConfig;
import judgels.sandalphon.api.problem.programming.ProblemLimits;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.editorial.ProblemEditorialStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementStore;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.bundle.ItemProcessorRegistry;
import judgels.sandalphon.problem.bundle.item.BundleItemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.role.RoleChecker;

@Singleton
public class ProblemClient {
    private final JudgelsAppConfiguration appConfig;
    private final RoleChecker roleChecker;
    private final ProblemStore problemStore;
    private final ProblemStatementStore statementStore;
    private final ProblemEditorialStore editorialStore;
    private final ProblemTagStore tagStore;
    private final ProgrammingProblemStore programmingProblemStore;
    private final BundleItemStore bundleItemStore;
    private final ItemProcessorRegistry itemProcessorRegistry;

    @Inject
    public ProblemClient(
            JudgelsAppConfiguration appConfig,
            RoleChecker roleChecker,
            ProblemStore problemStore,
            ProblemStatementStore statementStore,
            ProblemEditorialStore editorialStore,
            ProblemTagStore tagStore,
            ProgrammingProblemStore programmingProblemStore,
            BundleItemStore bundleItemStore,
            ItemProcessorRegistry itemProcessorRegistry) {

        this.appConfig = appConfig;
        this.roleChecker = roleChecker;
        this.problemStore = problemStore;
        this.statementStore = statementStore;
        this.editorialStore = editorialStore;
        this.tagStore = tagStore;
        this.programmingProblemStore = programmingProblemStore;
        this.bundleItemStore = bundleItemStore;
        this.itemProcessorRegistry = itemProcessorRegistry;
    }

    public Map<String, String> translateAllowedSlugsToJids(String actorJid, Set<String> slugs) {
        Optional<String> userJid = roleChecker.isAdmin(actorJid)
                ? Optional.empty()
                : Optional.of(actorJid);
        return problemStore.translateAllowedSlugsToJids(userJid, slugs);
    }

    public Set<String> getProblemJidsByTags(Set<String> tags) {
        return tagStore.filterProblemJidsByTags(null, tags);
    }

    public void setProblemVisibilityTagsByJids(Map<String, Boolean> problemVisibilitiesMap) {
        for (Map.Entry<String, Boolean> entry : problemVisibilitiesMap.entrySet()) {
            tagStore.updateVisibilityTag(entry.getKey(), entry.getValue());
        }
    }

    public ProblemInfo getProblem(String problemJid) {
        Problem problem = problemStore.getProblemByJid(problemJid).get();

        return new ProblemInfo.Builder()
                .slug(problem.getSlug())
                .type(ProblemType.valueOf(problem.getType().name()))
                .defaultLanguage(simplifyLanguageCode(statementStore.getStatementDefaultLanguage(null, problemJid)))
                .titlesByLanguage(statementStore.getTitlesByLanguage(null, problemJid).entrySet()
                        .stream()
                        .collect(toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    public ProblemMetadata getProblemMetadata(String problemJid) {
        return new ProblemMetadata.Builder()
                .hasEditorial(editorialStore.hasEditorial(null, problemJid))
                .tags(tagStore.findTopicTags(problemJid))
                .settersMap(problemStore.getProblemSetters(problemJid))
                .build();
    }

    public Map<String, ProblemMetadata> getProblemMetadatas(Set<String> problemJids) {
        return problemJids.stream().collect(toMap(jid -> jid, this::getProblemMetadata));
    }

    public Map<String, ProblemInfo> getProblems(Set<String> problemJids) {
        return problemJids.stream().collect(toMap(jid -> jid, this::getProblem));
    }

    public Map<String, String> getProblemNames(Set<String> problemJids, Optional<String> language) {
        return getProblems(problemJids)
                .entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> SandalphonUtils.getProblemName(e.getValue(), language)
                ));
    }

    public Optional<Item> getItem(String problemJid, String itemJid) {
        judgels.sandalphon.api.problem.bundle.ProblemWorksheet
                worksheet = getBundleProblemWorksheet(problemJid, Optional.empty());
        return worksheet.getItems().stream()
                .filter(item -> itemJid.equals(item.getJid()))
                .findAny();
    }

    public Map<String, Item> getItems(Set<String> problemJids, Set<String> itemJids) {
        Map<String, Item> itemsByItemJid = new HashMap<>();
        for (String problemJid : problemJids) {
            judgels.sandalphon.api.problem.bundle.ProblemWorksheet
                    worksheet = getBundleProblemWorksheet(problemJid, Optional.empty());
            worksheet.getItems().stream()
                    .filter(item -> itemJids.contains(item.getJid()))
                    .forEach(item -> itemsByItemJid.put(item.getJid(), item));
        }
        return itemsByItemJid;
    }

    public ProblemSubmissionConfig getProgrammingProblemSubmissionConfig(String problemJid) {
        return new ProblemSubmissionConfig.Builder()
                .gradingEngine(programmingProblemStore.getGradingEngine(null, problemJid))
                .gradingLanguageRestriction(programmingProblemStore.getLanguageRestriction(null, problemJid))
                .sourceKeys(programmingProblemStore.getGradingConfig(null, problemJid).getSourceFileFields())
                .build();
    }

    public judgels.sandalphon.api.problem.programming.ProblemWorksheet getProgrammingProblemWorksheet(
            String problemJid,
            Optional<String> language) {

        GradingConfig config = programmingProblemStore.getGradingConfig(null, problemJid);
        String sanitizedLanguage = sanitizeStatementLanguage(problemJid, language);
        ProblemStatement statement = statementStore.getStatement(null, problemJid, sanitizedLanguage);

        return new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(SandalphonUtils.replaceProblemRenderUrls(statement.getText(), appConfig.getBaseUrl(), problemJid))
                        .build())
                .limits(new ProblemLimits.Builder()
                        .timeLimit(config.getTimeLimit())
                        .memoryLimit(config.getMemoryLimit())
                        .build())
                .submissionConfig(getProgrammingProblemSubmissionConfig(problemJid))
                .build();
    }

    public judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheet(
            String problemJid,
            Optional<String> language) {

        String sanitizedLanguage = sanitizeStatementLanguage(problemJid, language);
        String defaultLanguage = statementStore.getStatementDefaultLanguage(null, problemJid);

        List<BundleItem> items = bundleItemStore.getNumberedItems(null, problemJid);
        List<Item> itemsWithConfig = new ArrayList<>();
        for (BundleItem item : items) {
            ItemConfig config = bundleItemStore.getItemConfig(null, problemJid, item, sanitizedLanguage, defaultLanguage);
            Item itemWithConfig = new Item.Builder()
                    .jid(item.getJid())
                    .type(item.getType())
                    .number(item.getNumber())
                    .meta(item.getMeta())
                    .config(config)
                    .build();
            itemsWithConfig.add(itemWithConfig);
        }

        ProblemStatement statement = statementStore.getStatement(null, problemJid, sanitizedLanguage);

        return new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(SandalphonUtils.replaceProblemRenderUrls(statement.getText(), appConfig.getBaseUrl(), problemJid))
                        .build())
                .items(itemsWithConfig
                        .stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).replaceRenderUrls(item, appConfig.getBaseUrl(), problemJid))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheetWithoutAnswerKey(
            String problemJid,
            Optional<String> language) {

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet worksheet =
                getBundleProblemWorksheet(problemJid, language);

        return new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .items(worksheet.getItems().stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).removeAnswerKey(item))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public Optional<ProblemEditorialInfo> getProblemEditorial(String problemJid, Optional<String> language) {
        if (!editorialStore.hasEditorial(null, problemJid)) {
            return Optional.empty();
        }

        String sanitizedLanguage = sanitizeEditorialLanguage(problemJid, language);
        ProblemEditorial editorial = editorialStore.getEditorial(null, problemJid, sanitizedLanguage);

        return Optional.of(new ProblemEditorialInfo.Builder()
                .text(SandalphonUtils.replaceProblemEditorialRenderUrls(editorial.getText(), appConfig.getBaseUrl(), problemJid))
                .defaultLanguage(simplifyLanguageCode(editorialStore.getEditorialDefaultLanguage(null, problemJid)))
                .languages(editorialStore.getEditorialLanguages(null, problemJid).stream()
                        .map(lang -> simplifyLanguageCode(lang))
                        .collect(Collectors.toSet()))
                .build());
    }

    public Map<String, ProblemEditorialInfo> getProblemEditorials(Set<String> problemJids, Optional<String> language) {
        Map<String, ProblemEditorialInfo> editorialsMap = new HashMap<>();
        for (String problemJid : problemJids) {
            Optional<ProblemEditorialInfo> editorial = getProblemEditorial(problemJid, language);
            if (editorial.isPresent()) {
                editorialsMap.put(problemJid, editorial.get());
            }
        }
        return Collections.unmodifiableMap(editorialsMap);
    }

    private String sanitizeStatementLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = statementStore.getStatementAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(statementStore.getStatementDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }

    private String sanitizeEditorialLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = editorialStore.getEditorialAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(editorialStore.getEditorialDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }
}
