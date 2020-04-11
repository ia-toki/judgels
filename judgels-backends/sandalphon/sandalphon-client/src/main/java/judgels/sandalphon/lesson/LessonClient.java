package judgels.sandalphon.lesson;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.sandalphon.SandalphonUtils;
import judgels.sandalphon.api.SandalphonClientConfiguration;
import judgels.sandalphon.api.client.lesson.ClientLessonService;
import judgels.sandalphon.api.lesson.LessonInfo;
import judgels.sandalphon.api.lesson.LessonStatement;
import judgels.service.api.client.BasicAuthHeader;

public class LessonClient {
    private final SandalphonClientConfiguration sandalphonConfig;
    private final BasicAuthHeader sandalphonClientAuthHeader;
    private final ClientLessonService clientLessonService;

    private final LoadingCache<String, LessonInfo> lessonCache;

    @Inject
    public LessonClient(
            SandalphonClientConfiguration sandalphonConfig,
            @Named("sandalphon") BasicAuthHeader sandalphonClientAuthHeader,
            ClientLessonService clientLessonService) {

        this.sandalphonConfig = sandalphonConfig;
        this.sandalphonClientAuthHeader = sandalphonClientAuthHeader;
        this.clientLessonService = clientLessonService;

        this.lessonCache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofSeconds(10))
                .build(new LessonCacheLoader());
    }

    public Map<String, String> translateAllowedSlugsToJids(String actorJid, Set<String> slugs) {
        return slugs.isEmpty()
                ? ImmutableMap.of()
                : clientLessonService.translateAllowedSlugsToJids(sandalphonClientAuthHeader, actorJid, slugs);
    }

    public LessonInfo getLesson(String lessonJid) {
        return lessonCache.get(lessonJid);
    }

    public Map<String, LessonInfo> getLessons(Set<String> lessonJids) {
        return lessonCache.getAll(lessonJids);
    }

    public LessonStatement getLessonStatement(String lessonJid) {
        LessonStatement statement = clientLessonService.getLessonStatement(sandalphonClientAuthHeader, lessonJid);
        return new LessonStatement.Builder()
                .from(statement)
                .text(SandalphonUtils.replaceLessonRenderUrls(
                        statement.getText(),
                        sandalphonConfig.getBaseUrl(),
                        lessonJid))
                .build();
    }

    private class LessonCacheLoader implements CacheLoader<String, LessonInfo> {
        @Nullable
        @Override
        public LessonInfo load(@Nonnull String lessonJid) {
            return clientLessonService.getLesson(sandalphonClientAuthHeader, lessonJid);
        }

        @Nonnull
        @Override
        public Map<String, LessonInfo> loadAll(@Nonnull Iterable<? extends String> lessonJids) {
            return clientLessonService.getLessons(sandalphonClientAuthHeader, ImmutableSet.copyOf(lessonJids));
        }
    }
}
