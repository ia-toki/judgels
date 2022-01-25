package judgels.jerahmeel.stats;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
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
import judgels.jerahmeel.api.stats.UserStats;
import judgels.jerahmeel.api.stats.UserTopStatsEntry;
import judgels.jerahmeel.persistence.ChapterProblemDao;
import judgels.jerahmeel.persistence.ChapterProblemModel;
import judgels.jerahmeel.persistence.CourseChapterDao;
import judgels.jerahmeel.persistence.CourseChapterModel;
import judgels.jerahmeel.persistence.ProblemSetProblemDao;
import judgels.jerahmeel.persistence.StatsUserCourseDao;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.jerahmeel.persistence.StatsUserProblemSetDao;
import judgels.jerahmeel.persistence.StatsUserProblemSetModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public class StatsStore {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final StatsUserCourseDao statsUserCourseDao;
    private final StatsUserProblemDao statsUserProblemDao;
    private final StatsUserProblemSetDao statsUserProblemSetDao;
    private final StatsUserDao statsUserDao;

    @Inject
    public StatsStore(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            StatsUserCourseDao statsUserCourseDao,
            StatsUserProblemDao statsUserProblemDao,
            StatsUserProblemSetDao statsUserProblemSetDao,
            StatsUserDao statsUserDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.statsUserCourseDao = statsUserCourseDao;
        this.statsUserProblemDao = statsUserProblemDao;
        this.statsUserProblemSetDao = statsUserProblemSetDao;
        this.statsUserDao = statsUserDao;
    }

    public Map<String, CourseProgress> getCourseProgressesMap(String userJid, Set<String> courseJids) {
        List<CourseChapterModel> courseChapters = courseChapterDao.selectAllByCourseJids(courseJids);
        Set<String> chapterJids = courseChapters.stream().map(m -> m.chapterJid).collect(toSet());

        Map<String, Long> chapterTotalProblemsMap = chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids)
                .stream()
                .collect(groupingBy(m -> m.chapterJid, counting()));

        Map<String, Long> courseTotalChaptersMap = courseChapters.stream()
                .collect(groupingBy(m -> m.courseJid, counting()));
        Map<String, Long> courseTotalSolvableChaptersMap = courseChapters.stream()
                .filter(m -> chapterTotalProblemsMap.getOrDefault(m.chapterJid, 0L) > 0)
                .collect(groupingBy(m -> m.courseJid, counting()));
        Map<String, Integer> courseSolvedChaptersMap = statsUserCourseDao
                .selectAllByUserJidAndCourseJids(userJid, courseJids)
                .stream()
                .collect(toMap(m -> m.courseJid, m -> m.progress));

        return courseJids.stream().collect(toMap(
                Function.identity(),
                jid -> new CourseProgress.Builder()
                        .solvedChapters(courseSolvedChaptersMap.getOrDefault(jid, 0))
                        .totalChapters(courseTotalChaptersMap.getOrDefault(jid, 0L).intValue())
                        .totalSolvableChapters(courseTotalSolvableChaptersMap.getOrDefault(jid, 0L).intValue())
                        .build()));
    }

    public Map<String, ChapterProgress> getChapterProgressesMap(String userJid, Set<String> chapterJids) {
        Map<String, Integer> totalProblemsMap = getChapterTotalProblemsMap(chapterJids);
        Map<String, Integer> solvedProblemsMap = getUserChapterSolvedProblemsMap(ImmutableSet.of(userJid), chapterJids)
                .get(userJid);

        return chapterJids.stream().collect(toMap(
                Function.identity(),
                jid -> new ChapterProgress.Builder()
                        .totalProblems(totalProblemsMap.get(jid))
                        .solvedProblems(solvedProblemsMap.get(jid))
                        .build()));
    }

    public Map<String, Integer> getChapterTotalProblemsMap(Set<String> chapterJids) {
        Map<String, Long> totalProblemsMap = chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids).stream()
                .collect(groupingBy(m -> m.chapterJid, counting()));

        return chapterJids.stream().collect(toMap(
                Function.identity(),
                jid -> totalProblemsMap.getOrDefault(jid, 0L).intValue()));
    }

    public Map<String, Map<String, Integer>> getUserChapterSolvedProblemsMap(
            Set<String> userJids,
            Set<String> chapterJids) {

        List<ChapterProblemModel> chapterProblems = chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids);
        Map<String, Set<String>> chapterProblemJidsMap = chapterProblems.stream()
                .collect(groupingBy(m -> m.chapterJid,  mapping(m -> m.problemJid, toSet())));

        Set<String> problemJids = chapterProblems.stream().map(m -> m.problemJid).collect(toSet());
        Map<String, Set<String>> userSolvedProblemJidsMap = statsUserProblemDao
                .selectAllByUserJidsAndProblemJids(userJids, problemJids)
                .stream()
                .filter(m -> m.verdict.equals(Verdict.ACCEPTED.getCode()))
                .collect(groupingBy(m -> m.userJid, mapping(m -> m.problemJid, toSet())));

        return userJids.stream().collect(toMap(
                Function.identity(),
                userJid -> chapterJids.stream().collect(toMap(
                        Function.identity(),
                        chapterJid -> Sets.intersection(
                                        userSolvedProblemJidsMap.getOrDefault(userJid, emptySet()),
                                        chapterProblemJidsMap.getOrDefault(chapterJid, emptySet())).size()))));
    }

    public Map<String, ProblemProgress> getProblemProgressesMap(String userJid, Set<String> problemJids) {
        List<StatsUserProblemModel> models = statsUserProblemDao.selectAllByUserJidAndProblemJids(userJid, problemJids);
        Map<String, StatsUserProblemModel> modelsMap = models.stream().collect(toMap(m -> m.problemJid, m -> m));

        return problemJids.stream().collect(toMap(
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

        return problemJids.stream().collect(toMap(
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
        Map<String, StatsUserProblemSetModel> modelsMap = models.stream().collect(toMap(m -> m.problemSetJid, m -> m));

        return problemSetJids.stream().collect(toMap(
                Function.identity(),
                jid -> new ProblemSetProgress.Builder()
                        .score(Optional.ofNullable(modelsMap.get(jid)).map(m -> m.score).orElse(0))
                        .totalProblems(totalProblemsMap.getOrDefault(jid, 0L).intValue())
                        .build()));
    }

    public Map<String, Map<String, ProblemProgress>> getUserProblemProgressesMap(
            Set<String> userJids,
            Set<String> problemJids) {

        List<StatsUserProblemModel> models =
                statsUserProblemDao.selectAllByUserJidsAndProblemJids(userJids, problemJids);

        Map<String, Map<String, ProblemProgress>> progressesMap = new HashMap<>();
        for (String userJid : userJids) {
            progressesMap.put(userJid, new HashMap<>());
        }
        for (StatsUserProblemModel m : models) {
            progressesMap.get(m.userJid).put(m.problemJid, new ProblemProgress.Builder()
                    .verdict(m.verdict)
                    .score(m.score)
                    .build());
        }
        return ImmutableMap.copyOf(progressesMap);
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

    public Page<UserTopStatsEntry> getTopUserStats(Optional<Integer> page, Optional<Integer> pageSize) {
        SelectionOptions.Builder options = new SelectionOptions.Builder()
                .from(SelectionOptions.DEFAULT_PAGED)
                .orderBy("score")
                .orderDir(OrderDir.DESC)
                .orderBy2("updatedAt")
                .orderDir2(OrderDir.ASC);

        page.ifPresent(options::page);
        pageSize.ifPresent(options::pageSize);

        return statsUserDao.selectPaged(options.build())
                .mapPage(models -> Lists.transform(models, m ->
                        new UserTopStatsEntry.Builder().userJid(m.userJid).totalScores(m.score).build()));
    }
}
