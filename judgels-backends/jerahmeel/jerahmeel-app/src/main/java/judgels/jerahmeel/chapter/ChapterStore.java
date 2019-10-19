package judgels.jerahmeel.chapter;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.ChapterInfo;
import judgels.jerahmeel.persistence.ChapterDao;

public class ChapterStore {
    private final ChapterDao chapterDao;

    @Inject
    public ChapterStore(ChapterDao chapterDao) {
        this.chapterDao = chapterDao;
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
}
