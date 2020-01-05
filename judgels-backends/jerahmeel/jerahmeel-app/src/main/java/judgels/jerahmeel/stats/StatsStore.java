package judgels.jerahmeel.stats;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.gabriel.api.Verdict;
import judgels.jerahmeel.api.chapter.ChapterProgress;
import judgels.jerahmeel.api.course.CourseProgress;
import judgels.jerahmeel.api.problem.ProblemProgress;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.jerahmeel.api.problem.ProblemTopStats;
import judgels.jerahmeel.api.problem.ProblemTopStatsEntry;
import judgels.jerahmeel.api.problemset.ProblemSetProgress;
import judgels.jerahmeel.api.user.UserStats;
import judgels.jerahmeel.api.user.UserTopStats;
import judgels.jerahmeel.api.user.UserTopStatsEntry;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.StatsUserChapterDao;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetDao;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;

public class StatsStore {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final StatsUserCourseDao statsUserCourseDao;
    private final StatsUserChapterDao statsUserChapterDao;
    private final StatsUserProblemDao statsUserProblemDao;
    private final StatsUserProblemSetDao statsUserProblemSetDao;
    private final StatsUserDao statsUserDao;

    @Inject
    public StatsStore(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            StatsUserCourseDao statsUserCourseDao,
            StatsUserChapterDao statsUserChapterDao,
            StatsUserProblemDao statsUserProblemDao,
            StatsUserProblemSetDao statsUserProblemSetDao,
            StatsUserDao statsUserDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.statsUserCourseDao = statsUserCourseDao;
        this.statsUserChapterDao = statsUserChapterDao;
        this.statsUserProblemDao = statsUserProblemDao;
        this.statsUserProblemSetDao = statsUserProblemSetDao;
        this.statsUserDao = statsUserDao;
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
                                .map(m -> m.verdict).orElse(Verdict.PENDING.getCode()))
                        .score(Optional.ofNullable(modelsMap.get(jid))
                                .map(m -> m.score).orElse(0))
                        .build()));
    }

    public Map<String, ProblemStats> getProblemStatsMap(Set<String> problemJids) {
        Map<String, Long> totalScoresMap = statsUserProblemDao.selectTotalScoresByProblemJids(problemJids);
        Map<String, Long> totalUsersAccepted = statsUserProblemDao.selectCountsAcceptedByProblemJids(problemJids);
        Map<String, Long> totalUsersTried = statsUserProblemDao.selectCountsTriedByProblemJids(problemJids);

        return problemJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ProblemStats.Builder()
                        .totalScores(totalScoresMap.getOrDefault(jid, 0L).intValue())
                        .totalUsersAccepted(totalUsersAccepted.getOrDefault(jid, 0L).intValue())
                        .totalUsersTried(totalUsersTried.getOrDefault(jid, 0L).intValue())
                        .build()));
    }

    public ProblemTopStats getProblemTopStats(String problemJid) {
        List<ProblemTopStatsEntry> topUsersByScore =
                statsUserProblemDao.selectAllByProblemJid(problemJid, new SelectionOptions.Builder()
                        .orderBy("score")
                        .orderDir(OrderDir.DESC)
                        .orderBy2("updatedAt")
                        .orderDir2(OrderDir.ASC)
                        .pageSize(5)
                        .build()).stream().map(m -> new ProblemTopStatsEntry.Builder()
                        .userJid(m.userJid)
                        .stats(m.score)
                        .build()).collect(Collectors.toList());

        List<ProblemTopStatsEntry> topUsersByTime =
                statsUserProblemDao.selectAllAcceptedByProblemJid(problemJid, new SelectionOptions.Builder()
                        .orderBy("time")
                        .orderDir(OrderDir.ASC)
                        .orderBy2("updatedAt")
                        .pageSize(5)
                        .build()).stream().map(m -> new ProblemTopStatsEntry.Builder()
                        .userJid(m.userJid)
                        .stats(m.time)
                        .build()).collect(Collectors.toList());

        List<ProblemTopStatsEntry> topUsersByMemory =
                statsUserProblemDao.selectAllAcceptedByProblemJid(problemJid, new SelectionOptions.Builder()
                        .orderBy("memory")
                        .orderDir(OrderDir.ASC)
                        .orderBy2("updatedAt")
                        .pageSize(5)
                        .build()).stream().map(m -> new ProblemTopStatsEntry.Builder()
                        .userJid(m.userJid)
                        .stats(m.memory)
                        .build()).collect(Collectors.toList());

        return new ProblemTopStats.Builder()
                .topUsersByScore(topUsersByScore)
                .topUsersByTime(topUsersByTime)
                .topUsersByMemory(topUsersByMemory)
                .build();
    }

    public Map<String, ProblemSetProgress> getProblemSetProgressesMap(String userJid, Set<String> problemSetJids) {
        Map<String, Long> totalProblemsMap = problemSetProblemDao.selectCountsByProblemSetJids(problemSetJids);
        List<StatsUserProblemSetModel> models =
                statsUserProblemSetDao.selectAllByUserJidAndProblemSetJids(userJid, problemSetJids);
        Map<String, StatsUserProblemSetModel> modelsMap =
                models.stream().collect(Collectors.toMap(m -> m.problemSetJid, m -> m));

        return problemSetJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ProblemSetProgress.Builder()
                        .score(Optional.ofNullable(modelsMap.get(jid)).map(m -> m.score).orElse(0))
                        .totalProblems(totalProblemsMap.getOrDefault(jid, 0L).intValue())
                        .build()));
    }

    public UserStats getUserStats(String userJid) {
        int totalScores = statsUserDao.selectByUserJid(userJid).map(m -> m.score).orElse(0);
        int totalProblemsTried = (int) statsUserProblemDao.selectCountTriedByUserJid(userJid);
        Map<String, Long> totalProblemVerdictsMap = statsUserProblemDao.selectCountsVerdictByUserJid(userJid);

        return new UserStats.Builder()
                .totalScores(totalScores)
                .totalProblemsTried(totalProblemsTried)
                .totalProblemVerdictsMap(totalProblemVerdictsMap)
                .build();
    }

    public UserTopStats getTopUserStats(Optional<Integer> page, Optional<Integer> pageSize) {
        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("score")
                .orderDir(OrderDir.DESC)
                .orderBy2("updatedAt")
                .orderDir2(OrderDir.ASC);

        page.ifPresent(options::page);
        pageSize.ifPresent(options::pageSize);

        List<UserTopStatsEntry> entries = statsUserDao.selectPaged(options.build()).getPage().stream()
                .map(m -> new UserTopStatsEntry.Builder().userJid(m.userJid).totalScores(m.score).build())
                .collect(Collectors.toList());
        return new UserTopStats.Builder().topUsers(entries).build();
    }
}
