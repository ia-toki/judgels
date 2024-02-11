package judgels.sandalphon;

import static java.util.stream.Collectors.toMap;
import static judgels.sandalphon.resource.LanguageUtils.simplifyLanguageCode;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import judgels.gabriel.api.GradingConfig;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
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
import judgels.sandalphon.api.problem.programming.ProblemSkeleton;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.lesson.LessonStore;
import judgels.sandalphon.lesson.statement.LessonStatementStore;
import judgels.sandalphon.problem.base.ProblemStore;
import judgels.sandalphon.problem.base.editorial.ProblemEditorialStore;
import judgels.sandalphon.problem.base.statement.ProblemStatementStore;
import judgels.sandalphon.problem.base.tag.ProblemTagStore;
import judgels.sandalphon.problem.bundle.ItemProcessorRegistry;
import judgels.sandalphon.problem.bundle.item.BundleItemStore;
import judgels.sandalphon.problem.programming.ProgrammingProblemStore;
import judgels.sandalphon.resource.StatementLanguageStatus;
import judgels.sandalphon.role.RoleChecker;

public class SandalphonClient {
    @Inject protected RoleChecker roleChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemStatementStore problemStatementStore;
    @Inject protected ProblemEditorialStore problemEditorialStore;
    @Inject protected ProblemTagStore problemTagStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected BundleItemStore bundleItemStore;
    @Inject protected ItemProcessorRegistry itemProcessorRegistry;
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonStatementStore lessonStatementStore;

    @Inject public SandalphonClient() {}

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
                        e -> SandalphonUtils.getProblemName(e.getValue(), language)
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

    public judgels.sandalphon.api.problem.programming.ProblemWorksheet getProgrammingProblemWorksheet(
            HttpServletRequest req,
            UriInfo uriInfo,
            String problemJid,
            Optional<String> language) {

        GradingConfig config = programmingProblemStore.getGradingConfig(null, problemJid);
        String sanitizedLanguage = sanitizeProblemStatementLanguage(problemJid, language);
        ProblemStatement statement = problemStatementStore.getStatement(null, problemJid, sanitizedLanguage);
        String apiUrl = getApiUrl(req, uriInfo);

        return new judgels.sandalphon.api.problem.programming.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(SandalphonUtils.replaceProblemRenderUrls(statement.getText(), apiUrl, problemJid))
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

    public judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheet(
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
        String apiUrl = getApiUrl(req, uriInfo);

        return new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .statement(new ProblemStatement.Builder()
                        .from(statement)
                        .text(SandalphonUtils.replaceProblemRenderUrls(statement.getText(), apiUrl, problemJid))
                        .build())
                .items(itemsWithConfig
                        .stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).replaceRenderUrls(item, apiUrl, problemJid))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheetWithoutAnswerKey(
            HttpServletRequest req,
            UriInfo uriInfo,
            String problemJid,
            Optional<String> language) {

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet worksheet = getBundleProblemWorksheet(req, uriInfo, problemJid, language);
        return new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .items(worksheet.getItems().stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).removeAnswerKey(item))
                        .collect(Collectors.toList())
                )
                .build();
    }

    public Optional<ProblemEditorialInfo> getProblemEditorial(String problemJid, URI baseUri, Optional<String> language) {
        if (!problemEditorialStore.hasEditorial(null, problemJid)) {
            return Optional.empty();
        }

        String sanitizedLanguage = sanitizeProblemEditorialLanguage(problemJid, language);
        ProblemEditorial editorial = problemEditorialStore.getEditorial(null, problemJid, sanitizedLanguage);

        return Optional.of(new ProblemEditorialInfo.Builder()
                .text(SandalphonUtils.replaceProblemEditorialRenderUrls(editorial.getText(), baseUri.toString(), problemJid))
                .defaultLanguage(simplifyLanguageCode(problemEditorialStore.getEditorialDefaultLanguage(null, problemJid)))
                .languages(problemEditorialStore.getEditorialLanguages(null, problemJid).stream()
                        .map(lang -> simplifyLanguageCode(lang))
                        .collect(Collectors.toSet()))
                .build());
    }

    public Map<String, ProblemEditorialInfo> getProblemEditorials(Collection<String> problemJids, URI baseUri, Optional<String> language) {
        Map<String, ProblemEditorialInfo> editorialsMap = new HashMap<>();
        for (String problemJid : Set.copyOf(problemJids)) {
            Optional<ProblemEditorialInfo> editorial = getProblemEditorial(problemJid, baseUri, language);
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

    public Map<String, String> translateAllowedLessonSlugsToJids(String actorJid, Collection<String> slugs) {
        Optional<String> userJid = roleChecker.isAdmin(actorJid)
                ? Optional.empty()
                : Optional.of(actorJid);
        return lessonStore.translateAllowedSlugsToJids(userJid, slugs);
    }

    public LessonInfo getLesson(String lessonJid) {
        Lesson lesson = lessonStore.getLessonByJid(lessonJid).get();

        return new LessonInfo.Builder()
                .slug(lesson.getSlug())
                .defaultLanguage(simplifyLanguageCode(lessonStatementStore.getDefaultLanguage(null, lessonJid)))
                .titlesByLanguage(lessonStatementStore.getTitlesByLanguage(null, lessonJid).entrySet()
                        .stream()
                        .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getValue())))
                .build();
    }

    public Map<String, LessonInfo> getLessons(Collection<String> lessonJids) {
        return Set.copyOf(lessonJids).stream().collect(Collectors.toMap(jid -> jid, this::getLesson));
    }

    public LessonStatement getLessonStatement(
            HttpServletRequest req,
            UriInfo uriInfo,
            String lessonJid, Optional<String> language) {

        String sanitizedLanguage = sanitizeLessonStatementLanguage(lessonJid, language);
        LessonStatement statement = lessonStatementStore.getStatement(null, lessonJid, sanitizedLanguage);
        String apiUrl = getApiUrl(req, uriInfo);

        return new LessonStatement.Builder()
                .from(statement)
                .text(SandalphonUtils.replaceLessonRenderUrls(statement.getText(), apiUrl, lessonJid))
                .build();
    }

    private String sanitizeLessonStatementLanguage(String problemJid, Optional<String> language) {
        Map<String, StatementLanguageStatus> availableLanguages = lessonStatementStore.getAvailableLanguages(null, problemJid);
        Map<String, String> simplifiedLanguages = availableLanguages.entrySet()
                .stream()
                .collect(Collectors.toMap(e -> simplifyLanguageCode(e.getKey()), e -> e.getKey()));

        String lang = language.orElse("");
        if (!simplifiedLanguages.containsKey(lang) || availableLanguages.get(simplifiedLanguages.get(lang)) == StatementLanguageStatus.DISABLED) {
            lang = simplifyLanguageCode(lessonStatementStore.getDefaultLanguage(null, problemJid));
        }

        return simplifiedLanguages.get(lang);
    }

    private static String getApiUrl(HttpServletRequest req, UriInfo uriInfo) {
        if (req == null) {
            return "";
        }

        String oldScheme = uriInfo.getBaseUri().getScheme();
        String newScheme = oldScheme;

        String forwardedProto = req.getHeader("X-Forwarded-Proto");
        if (forwardedProto != null && !forwardedProto.isEmpty()) {
            newScheme = forwardedProto;
        }

        return newScheme + uriInfo.getBaseUri().toString().substring(oldScheme.length());
    }
}
