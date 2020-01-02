package judgels.jerahmeel.stats;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.StatsUserChapterDao;

public class StatsStore {
    private final ChapterProblemDao chapterProblemDao;
    private final StatsUserChapterDao statsUserChapterDao;

    @Inject
    public StatsStore(
            ChapterProblemDao chapterProblemDao,
            StatsUserChapterDao statsUserChapterDao) {

        this.chapterProblemDao = chapterProblemDao;
        this.statsUserChapterDao = statsUserChapterDao;
    }

    public Map<String, ChapterProgress> getChapterProgressesMap(String userJid, Set<String> chapterJids) {
        Map<String, Long> totalProblemsMap = chapterProblemDao.selectCountProgrammingByChapterJids(chapterJids);
        Map<String, Integer> solvedProblemsMap =
                statsUserChapterDao.selectAllByUserJidAndChapterJids(userJid, chapterJids).stream()
                .collect(Collectors.toMap(m -> m.chapterJid, m -> m.progress));

        return chapterJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ChapterProgress.Builder()
                        .solvedProblems(Optional.ofNullable(solvedProblemsMap.get(jid)).orElse(0))
                        .totalProblems(Optional.ofNullable(totalProblemsMap.get(jid)).orElse(0L).intValue())
                        .build()));
    }
}
