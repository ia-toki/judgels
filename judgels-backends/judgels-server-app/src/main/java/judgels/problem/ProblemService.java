package judgels.problem;

import static java.util.stream.Collectors.toMap;
import static judgels.resource.LanguageUtils.simplifyLanguageCode;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import judgels.api.problem.Problem;
import judgels.api.problem.ProblemEditorial;
import judgels.api.problem.ProblemEditorialInfo;
import judgels.api.problem.ProblemInfo;
import judgels.api.problem.ProblemMetadata;
import judgels.api.problem.ProblemStatement;
import judgels.api.problem.ProblemType;
import judgels.api.problem.bundle.BundleItem;
import judgels.api.problem.bundle.Item;
import judgels.api.problem.bundle.ItemConfig;
import judgels.api.problem.programming.ProblemLimits;
import judgels.api.problem.programming.ProblemSkeleton;
import judgels.api.problem.programming.ProblemSubmissionConfig;
import judgels.gabriel.api.GradingConfig;
import judgels.gabriel.api.ScoringConfig;
import judgels.problem.base.ProblemStore;
import judgels.problem.base.editorial.ProblemEditorialStore;
import judgels.problem.base.statement.ProblemStatementStore;
import judgels.problem.base.tag.ProblemTagStore;
import judgels.problem.bundle.ItemProcessorRegistry;
import judgels.problem.bundle.item.BundleItemStore;
import judgels.problem.programming.ProgrammingProblemStore;
import judgels.resource.StatementLanguageStatus;
import judgels.role.ProblemAdminRoleChecker;

public class ProblemService {
    @Inject protected ProblemAdminRoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemStatementStore problemStatementStore;
    @Inject protected ProblemEditorialStore problemEditorialStore;
    @Inject protected ProblemTagStore problemTagStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected BundleItemStore bundleItemStore;
    @Inject protected ItemProcessorRegistry itemProcessorRegistry;

    @Inject public ProblemService() {}

    public Map<String, String> translateAllowedProblemSlugsToJids(String actorJid, Set<String> slugs) {
        Optional<String> userJid = roleChecker.isAdmin(actorJid)
                ? Optional.empty()
                : Optional.of(actorJid);
        return problemStore.translateAllowedSlugsToJids(userJid, slugs);
    }

    public Set<String> getProblemJidsByTags(Set<String> tags) {
        return problemTagStore.filterProblemJidsByTags(null, tags);
    }

    public void setProblemVisibilityTagsByJids(Map<String, Boolean> problemVisibilitiesMap) {
        for (Map.Entry<String, Boolean> entry : problemVisibilitiesMap.entrySet()) {
            problemTagStore.updateVisibilityTag(entry.getKey(), entry.getValue());
        }
    }

    public ProblemInfo getProblem(String problemJid) {
        Problem problem = problemStore.getProblemByJid(problemJid).get();

        return new ProblemInfo.Builder()
                .slug(problem.getSlug())
                .type(ProblemType.valueOf(problem.getType().name()))
                .defaultLanguage(simplifyLanguageCode(problemStatementStore.getStatementDefaultLanguage(null, problemJid)))
                .titlesByLanguage(problemStatementStore.getTitlesByLanguage(null, problemJid).entrySet()
                        .stream()
                        .collect(toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    public ProblemMetadata getProblemMetadata(String problemJid) {
        return new ProblemMetadata.Builder()
                .hasEditorial(problemEditorialStore.hasEditorial(null, problemJid))
                .tags(problemTagStore.findTopicTags(problemJid))
                .settersMap(problemStore.getProblemSetters(problemJid))
                .build();
    }

    public Map<String, ProblemMetadata> getProblemMetadatas(Collection<String> problemJids) {
        return Set.copyOf(problemJids).stream().collect(toMap(jid -> jid, this::getProblemMetadata));
    }

    public Map<String, ProblemInfo> getProblems(Collection<String> problemJids) {
        return Set.copyOf(problemJids).stream().collect(toMap(jid -> jid, this::getProblem));
    }

    public Map<String, String> getProblemNames(Collection<String> problemJids, Optional<String> language) {
        return getProblems(problemJids)
                .entrySet()
                .stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> ProblemUtils.getProblemName(e.getValue(), language)
                ));
    }

    public Optional<Item> getItem(String problemJid, String itemJid) {
        String defaultLanguage = problemStatementStore.getStatementDefaultLanguage(null, problemJid);
        for (BundleItem item : bundleItemStore.getNumberedItems(null, problemJid)) {
            if (item.getJid().equals(itemJid)) {
                ItemConfig config = bundleItemStore.getItemConfig(null, problemJid, item, defaultLanguage, defaultLanguage);
                return Optional.of(new Item.Builder()
                        .jid(item.getJid())
                        .type(item.getType())
                        .number(item.getNumber())
                        .meta(item.getMeta())
                        .config(config)
                        .build());
            }
        }

        return Optional.empty();
    }

    public Map<String, BundleItem> getItems(Collection<String> problemJids, Collection<String> itemJids) {
        Map<String, BundleItem> itemsByItemJid = new HashMap<>();
        for (String problemJid : Set.copyOf(problemJids)) {
            getItems(problemJid).stream()
                    .filter(item -> itemJids.contains(item.getJid()))
                    .forEach(item -> itemsByItemJid.put(item.getJid(), item));
        }
        return itemsByItemJid;
    }

    public List<BundleItem> getItems(String problemJid) {
        return bundleItemStore.getNumberedItems(null, problemJid);
    }

    public ProblemSubmissionConfig getProgrammingProblemSubmissionConfig(String problemJid) {
        return programmingProblemStore.getProgrammingProblemSubmissionConfig(problemJid);
    }

    public Map<String, ProblemSubmissionConfig> getProgrammingProblemSubmissionConfigs(Collection<String> problemJids) {
        return Set.copyOf(problemJids).stream().collect(toMap(jid -> jid, this::getProgrammingProblemSubmissionConfig));
    }

    public ScoringConfig getProgrammingProblemScoringConfig(String problemJid) {
        return programmingProblemStore.getProgrammingProblemScoringConfig(problemJid);
    }

    public Map<String, ScoringConfig> getProgrammingProblemScoringConfigs(Collection<String> problemJids) {
        return Set.copyOf(problemJids).stream().collect(toMap(jid -> jid, this::getProgrammingProblemScoringConfig));
    }

    public judgels.api.problem.programming.ProblemWorksheet getProgrammingProblemWorksheet(
            HttpServletRequest req,
            UriInfo uriInfo,
            String problemJid,
            Optional<String> language) {

        GradingConfig config = programmingProblemStore.getGradingConfig(null, problemJid);
        String sanitizedLanguage = sanitizeProblemStatementLanguage(problemJid, language);
        ProblemStatement statement = problemStatementStore.getStatement(null, problemJid, sanitizedLanguage);
        String apiUrl = ProblemUtils.getApiUrl(req, uriInfo);

        return new judgels.api.problem.programming.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(ProblemUtils.replaceProblemRenderUrls(statement.getText(), apiUrl, problemJid))
                        .build())
                .limits(new ProblemLimits.Builder()
                        .timeLimit(config.getTimeLimit())
                        .memoryLimit(config.getMemoryLimit())
                        .build())
                .submissionConfig(getProgrammingProblemSubmissionConfig(problemJid))
                .build();
    }

    public Set<ProblemSkeleton> getProgrammingProblemSkeletons(String problemJid) {
        return problemStatementStore.getSkeletons(null, problemJid);
    }

    public judgels.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheet(
            HttpServletRequest req,
            UriInfo uriInfo,
            String problemJid,
            Optional<String> language) {

        String sanitizedLanguage = sanitizeProblemStatementLanguage(problemJid, language);
        String defaultLanguage = problemStatementStore.getStatementDefaultLanguage(null, problemJid);

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

        ProblemStatement statement = problemStatementStore.getStatement(null, problemJid, sanitizedLanguage);
        String apiUrl = ProblemUtils.getApiUrl(req, uriInfo);

        return new judgels.api.problem.bundle.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(ProblemUtils.replaceProblemRenderUrls(statement.getText(), apiUrl, problemJid))
                        .build())
                .items(itemsWithConfig
                        .stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).replaceRenderUrls(item, apiUrl, problemJid))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public judgels.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheetWithoutAnswerKey(
            HttpServletRequest req,
            UriInfo uriInfo,
            String problemJid,
            Optional<String> language) {

        judgels.api.problem.bundle.ProblemWorksheet worksheet = getBundleProblemWorksheet(req, uriInfo, problemJid, language);
        return new judgels.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .items(worksheet.getItems().stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).removeAnswerKey(item))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public Optional<ProblemEditorialInfo> getProblemEditorial(HttpServletRequest req, UriInfo uriInfo, String problemJid, Optional<String> language) {
        if (!problemEditorialStore.hasEditorial(null, problemJid)) {
            return Optional.empty();
        }

        String sanitizedLanguage = sanitizeProblemEditorialLanguage(problemJid, language);
        ProblemEditorial editorial = problemEditorialStore.getEditorial(null, problemJid, sanitizedLanguage);
        String apiUrl = ProblemUtils.getApiUrl(req, uriInfo);

        return Optional.of(new ProblemEditorialInfo.Builder()
                .text(ProblemUtils.replaceProblemEditorialRenderUrls(editorial.getText(), apiUrl, problemJid))
                .defaultLanguage(simplifyLanguageCode(problemEditorialStore.getEditorialDefaultLanguage(null, problemJid)))
                .languages(problemEditorialStore.getEditorialLanguages(null, problemJid).stream()
                        .map(lang -> simplifyLanguageCode(lang))
                        .collect(Collectors.toSet()))
                .build());
    }

    public Map<String, ProblemEditorialInfo> getProblemEditorials(HttpServletRequest req, UriInfo uriInfo, Collection<String> problemJids, Optional<String> language) {
        Map<String, ProblemEditorialInfo> editorialsMap = new HashMap<>();
        for (String problemJid : Set.copyOf(problemJids)) {
            Optional<ProblemEditorialInfo> editorial = getProblemEditorial(req, uriInfo, problemJid, language);
            if (editorial.isPresent()) {
                editorialsMap.put(problemJid, editorial.get());
            }
        }
        return Collections.unmodifiableMap(editorialsMap);
    }

    private String sanitizeProblemStatementLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = problemStatementStore.getStatementAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(problemStatementStore.getStatementDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }

    private String sanitizeProblemEditorialLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = problemEditorialStore.getEditorialAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(problemEditorialStore.getEditorialDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }
}
