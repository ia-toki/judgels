package judgels.jerahmeel.chapter;

import static judgels.jerahmeel.JerahmeelCacheUtils.getShortDuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.persistence.ChapterDao;
import judgels.jerahmeel.persistence.ChapterModel;

public class ChapterStore {
    private final ChapterDao chapterDao;

    private final LoadingCache<String, Chapter> chapterByJidCache;

    @Inject
    public ChapterStore(ChapterDao chapterDao) {
        this.chapterDao = chapterDao;

        this.chapterByJidCache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(getShortDuration())
                .build(this::getChapterByJidUncached);
    }

    public Optional<Chapter> getChapterByJid(String chapterJid) {
        return Optional.ofNullable(chapterByJidCache.get(chapterJid));
    }

    private Chapter getChapterByJidUncached(String chapterJid) {
        return chapterDao.selectByJid(chapterJid).map(ChapterStore::fromModel).orElse(null);
    }


    public Map<String, ChapterInfo> getChapterInfosByJids(Set<String> chapterJids) {
        return chapterDao.selectByJids(chapterJids)
                .values()
                .stream()
                .collect(Collectors.toMap(
                        c -> c.jid,
                        c -> new ChapterInfo.Builder()
                                .name(c.name)
                                .build()));
    }


    private static Chapter fromModel(ChapterModel model) {
        return new Chapter.Builder()
                .id(model.id)
                .jid(model.jid)
                .name(model.name)
                .description(Optional.ofNullable(model.description))
                .build();
    }
}
