package judgels.jerahmeel.stats;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.api.Verdicts;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.jerahmeel.api.problem.ProblemStatsEntry;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class StatsStore {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final StatsUserCourseDao statsUserCourseDao;
    private final StatsUserChapterDao statsUserChapterDao;
    private final StatsUserProblemDao statsUserProblemDao;

    @Inject
    public StatsStore(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            StatsUserCourseDao statsUserCourseDao,
            StatsUserChapterDao statsUserChapterDao,
            StatsUserProblemDao statsUserProblemDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.statsUserCourseDao = statsUserCourseDao;
        this.statsUserChapterDao = statsUserChapterDao;
        this.statsUserProblemDao = statsUserProblemDao;
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
                        .solvedChapters(solvedChaptersMap.getOrDefault(jid, 0))
                        .totalChapters(totalChaptersMap.getOrDefault(jid, 0))
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
                        .solvedProblems(solvedProblemsMap.getOrDefault(jid, 0))
                        .totalProblems(totalProblemsMap.getOrDefault(jid, 0L).intValue())
                        .build()));
    }

    public Map<String, ProblemProgress> getProblemProgressesMap(String userJid, Set<String> problemJids) {
        List<StatsUserProblemModel> models = statsUserProblemDao.selectAllByUserJidAndProblemJids(userJid, problemJids);
        Map<String, StatsUserProblemModel> modelsMap =
                models.stream().collect(Collectors.toMap(m -> m.problemJid, m -> m));

        return problemJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ProblemProgress.Builder()
                        .verdict(Optional.ofNullable(modelsMap.get(jid))
                                .map(m -> Verdicts.fromCode(m.verdict)).orElse(Verdict.PENDING))
                        .score(Optional.ofNullable(modelsMap.get(jid))
                                .map(m -> m.score).orElse(0))
                        .build()));
    }

    public ProblemStats getProblemStats(String problemJid) {
        long totalUsersAccepted = statsUserProblemDao
                .selectCountsAcceptedByProblemJids(ImmutableSet.of(problemJid))
                .getOrDefault(problemJid, 0L);
        long totalUsersTried = statsUserProblemDao
                .selectCountsTriedByProblemJids(ImmutableSet.of(problemJid))
                .getOrDefault(problemJid, 0L);

        List<ProblemStatsEntry> topUsersByTime =
                statsUserProblemDao.selectAllByProblemJid(problemJid, new SelectionOptions.Builder()
                        .orderBy("time")
                        .orderDir(OrderDir.ASC)
                        .pageSize(5)
                        .build()).stream().map(m -> new ProblemStatsEntry.Builder()
                        .userJid(m.userJid)
                        .stats(m.time)
                        .build()).collect(Collectors.toList());

        List<ProblemStatsEntry> topUsersByMemory =
                statsUserProblemDao.selectAllByProblemJid(problemJid, new SelectionOptions.Builder()
                        .orderBy("memory")
                        .orderDir(OrderDir.ASC)
                        .pageSize(5)
                        .build()).stream().map(m -> new ProblemStatsEntry.Builder()
                        .userJid(m.userJid)
                        .stats(m.memory)
                        .build()).collect(Collectors.toList());

        return new ProblemStats.Builder()
                .totalUsersAccepted(totalUsersAccepted)
                .totalUsersTried(totalUsersTried)
                .topUsersByTime(topUsersByTime)
                .topUsersByMemory(topUsersByMemory)
                .build();
    }
}
