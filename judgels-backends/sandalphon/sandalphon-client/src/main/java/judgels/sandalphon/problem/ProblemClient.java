package judgels.sandalphon.problem;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.bundle.Item;
import judgels.sandalphon.api.problem.programming.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.programming.ProblemWorksheet;
import judgels.sandalphon.problem.bundle.ItemProcessorRegistry;
import judgels.service.api.client.BasicAuthHeader;

public class ProblemClient {
    private final SandalphonClientConfiguration sandalphonConfig;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientProblemService clientProblemService;
    private final ItemProcessorRegistry itemProcessorRegistry;

    private final LoadingCache<String, ProblemInfo> problemCache;

    @Inject
    public ProblemClient(
            SandalphonClientConfiguration sandalphonConfig,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader,
            ClientProblemService clientProblemService,
            ItemProcessorRegistry itemProcessorRegistry) {

        this.sandalphonConfig = sandalphonConfig;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientProblemService = clientProblemService;
        this.itemProcessorRegistry = itemProcessorRegistry;

        this.problemCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofSeconds(10))
                .build(new ProblemCacheLoader());
    }

    public Map<String, String> translateAllowedSlugsToJids(String actorJid, Set<String> slugs) {
        return slugs.isEmpty()
                ? ImmutableMap.of()
                : clientProblemService.translateAllowedSlugsToJids(sandalphonClientAuthHeader, actorJid, slugs);
    }

    public ProblemInfo getProblem(String problemJid) {
        return problemCache.get(problemJid);
    }

    public Map<String, ProblemInfo> getProblems(Set<String> problemJids) {
        return problemCache.getAll(problemJids);
    }

    public Map<String, String> getProblemNames(Set<String> problemJids, Optional<String> language) {
        return getProblems(problemJids)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
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
        return clientProblemService.getProgrammingProblemSubmissionConfig(
                sandalphonClientAuthHeader, problemJid);
    }

    public ProblemWorksheet getProgrammingProblemWorksheet(String problemJid, Optional<String> language) {
        ProblemWorksheet worksheet = clientProblemService.getProgrammingProblemWorksheet(
                sandalphonClientAuthHeader, problemJid, language);

        return new ProblemWorksheet.Builder()
                .from(worksheet)
                .statement(new ProblemStatement.Builder()
                        .from(worksheet.getStatement())
                        .text(SandalphonUtils.replaceRenderUrls(
                                worksheet.getStatement().getText(),
                                sandalphonConfig.getBaseUrl(),
                                problemJid))
                        .build())
                .build();
    }

    public judgels.sandalphon.api.problem.bundle.ProblemWorksheet getBundleProblemWorksheet(
            String problemJid,
            Optional<String> language) {

        judgels.sandalphon.api.problem.bundle.ProblemWorksheet worksheet = clientProblemService
                .getBundleProblemWorksheet(sandalphonClientAuthHeader, problemJid, language);

        return new judgels.sandalphon.api.problem.bundle.ProblemWorksheet.Builder()
                .from(worksheet)
                .statement(new ProblemStatement.Builder()
                        .from(worksheet.getStatement())
                        .text(SandalphonUtils.replaceRenderUrls(
                                worksheet.getStatement().getText(),
                                sandalphonConfig.getBaseUrl(),
                                problemJid))
                        .build())
                .items(worksheet.getItems().stream()
                        .map(item -> itemProcessorRegistry.get(item.getType()).replaceRenderUrls(
                                item, sandalphonConfig.getBaseUrl(), problemJid))
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

    private class ProblemCacheLoader implements CacheLoader<String, ProblemInfo> {
        @Nullable
        @Override
        public ProblemInfo load(@Nonnull String problemJid) {
            return clientProblemService.getProblem(sandalphonClientAuthHeader, problemJid);
        }

        @Nonnull
        @Override
        public Map<String, ProblemInfo> loadAll(@Nonnull Iterable<? extends String> problemJids) {
            return clientProblemService.getProblems(sandalphonClientAuthHeader, ImmutableSet.copyOf(problemJids));
        }
    }
}
