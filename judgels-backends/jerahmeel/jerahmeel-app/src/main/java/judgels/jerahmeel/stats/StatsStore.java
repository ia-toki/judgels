package judgels.jerahmeel.stats;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserCourseDao;

public class StatsStore {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final StatsUserCourseDao statsUserCourseDao;
    private final StatsUserChapterDao statsUserChapterDao;

    @Inject
    public StatsStore(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            StatsUserCourseDao statsUserCourseDao,
            StatsUserChapterDao statsUserChapterDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.statsUserCourseDao = statsUserCourseDao;
        this.statsUserChapterDao = statsUserChapterDao;
    }

    public Map<String, CourseProgress> getCourseProgressesMap(String userJid, Set<String> courseJids) {
        Map<String, Integer> totalChaptersMap = courseJids.stream().collect(
                Collectors.toMap(Function.identity(), jid -> (int) courseChapterDao.selectCountByCourseJid(jid)));
        Map<String, Integer> solvedChaptersMap =
                statsUserCourseDao.selectAllByUserJidAndCourseJids(userJid, courseJids).stream()
                        .collect(Collectors.toMap(m -> m.courseJid, m -> m.progress));

        return courseJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new CourseProgress.Builder()
                        .solvedChapters(Optional.ofNullable(solvedChaptersMap.get(jid)).orElse(0))
                        .totalChapters(Optional.ofNullable(totalChaptersMap.get(jid)).orElse(0))
                        .build()));
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
