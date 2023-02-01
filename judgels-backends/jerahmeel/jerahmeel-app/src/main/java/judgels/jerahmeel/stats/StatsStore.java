package judgels.jerahmeel.stats;

import static java.util.Collections.emptySet;
import static judgels.persistence.api.SelectionOptions.DEFAULT_ALL;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.StatsUserDao;
import judgels.jerahmeel.persistence.StatsUserProblemDao;
import judgels.jerahmeel.persistence.StatsUserProblemModel;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.sandalphon.api.problem.ProblemType;

public class StatsStore {
    private final CourseChapterDao courseChapterDao;
    private final ChapterProblemDao chapterProblemDao;
    private final ProblemSetProblemDao problemSetProblemDao;
    private final StatsUserProblemDao statsUserProblemDao;
    private final StatsUserDao statsUserDao;

    @Inject
    public StatsStore(
            CourseChapterDao courseChapterDao,
            ChapterProblemDao chapterProblemDao,
            ProblemSetProblemDao problemSetProblemDao,
            StatsUserProblemDao statsUserProblemDao,
            StatsUserDao statsUserDao) {

        this.courseChapterDao = courseChapterDao;
        this.chapterProblemDao = chapterProblemDao;
        this.problemSetProblemDao = problemSetProblemDao;
        this.statsUserProblemDao = statsUserProblemDao;
        this.statsUserDao = statsUserDao;
    }

    public Map<String, CourseProgress> getCourseProgressesMap(String userJid, Set<String> courseJids) {
        Map<String, CourseProgress> progressesMap = new HashMap<>();
        for (String courseJid : courseJids) {
            Set<String> chapterJids = new HashSet<>();
            for (CourseChapterModel model : courseChapterDao.selectAllByCourseJid(courseJid, DEFAULT_ALL)) {
                chapterJids.add(model.chapterJid);
            }

            Set<String> problemJids = new HashSet<>();
            for (ChapterProblemModel model : chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids)) {
                problemJids.add(model.problemJid);
            }

            int solvedProblems = 0;
            for (StatsUserProblemModel model : statsUserProblemDao.selectAllByUserJidAndProblemJids(userJid, problemJids)) {
                if (model.verdict.equals(Verdict.ACCEPTED.getCode())) {
                    solvedProblems++;
                }
            }

            progressesMap.put(courseJid, new CourseProgress.Builder()
                    .solvedProblems(solvedProblems)
                    .totalProblems(problemJids.size())
                    .build());
        }
        return ImmutableMap.copyOf(progressesMap);
    }

    public Map<String, ChapterProgress> getChapterProgressesMap(String userJid, Set<String> chapterJids) {
        Map<String, Integer> totalProblemsMap = getChapterTotalProblemsMap(chapterJids);
        Map<String, Integer> solvedProblemsMap = getUserChapterSolvedProblemsMap(ImmutableSet.of(userJid), chapterJids).get(userJid);

        Map<String, ChapterProgress> chapterProgressesMap = new HashMap<>();
        for (String chapterJid : chapterJids) {
            chapterProgressesMap.put(chapterJid, new ChapterProgress.Builder()
                    .totalProblems(totalProblemsMap.get(chapterJid))
                    .solvedProblems(solvedProblemsMap.get(chapterJid))
                    .build());
        }
        return ImmutableMap.copyOf(chapterProgressesMap);
    }

    public Map<String, Integer> getChapterTotalProblemsMap(Set<String> chapterJids) {
        Map<String, Integer> totalProblemsMap = new HashMap<>();
        for (String chapterJid : chapterJids) {
            totalProblemsMap.put(chapterJid, 0);
        }
        for (ChapterProblemModel model : chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids)) {
            totalProblemsMap.put(model.chapterJid, 1 + totalProblemsMap.get(model.chapterJid));
        }
        return ImmutableMap.copyOf(totalProblemsMap);
    }

    public Map<String, Map<String, Integer>> getUserChapterSolvedProblemsMap(
            Set<String> userJids,
            Set<String> chapterJids) {

        Map<String, Set<String>> chapterProblemJidsMap = new HashMap<>();
        for (String chapterJid : chapterJids) {
            chapterProblemJidsMap.put(chapterJid, new HashSet<>());
        }
        Set<String> problemJids = new HashSet<>();
        for (ChapterProblemModel model : chapterProblemDao.selectAllProgrammingByChapterJids(chapterJids)) {
            chapterProblemJidsMap.get(model.chapterJid).add(model.problemJid);
            problemJids.add(model.problemJid);
        }

        Map<String, Set<String>> userSolvedProblemJidsMap = new HashMap<>();
        for (String userJid : userJids) {
            userSolvedProblemJidsMap.put(userJid, new HashSet<>());
        }
        for (StatsUserProblemModel model : statsUserProblemDao.selectAllByUserJidsAndProblemJids(userJids, problemJids)) {
            if (model.verdict.equals(Verdict.ACCEPTED.getCode())) {
                userSolvedProblemJidsMap.get(model.userJid).add(model.problemJid);
            }
        }

        Map<String, Map<String, Integer>> userChapterSolvedProblemsMap = new HashMap<>();
        for (String userJid : userJids) {
            userChapterSolvedProblemsMap.put(userJid, new HashMap<>());
            for (String chapterJid : chapterJids) {
                userChapterSolvedProblemsMap.get(userJid).put(chapterJid, Sets.intersection(
                        userSolvedProblemJidsMap.get(userJid),
                        chapterProblemJidsMap.get(chapterJid)).size());
            }
        }
        return ImmutableMap.copyOf(userChapterSolvedProblemsMap);
    }

    public Map<String, ProblemProgress> getProblemProgressesMap(String userJid, Set<String> problemJids) {
        Map<String, String> verdictsMap = new HashMap<>();
        Map<String, Integer> scoresMap = new HashMap<>();
        for (String problemJid : problemJids) {
            verdictsMap.put(problemJid, Verdict.PENDING.getCode());
            scoresMap.put(problemJid, 0);
        }
        for (StatsUserProblemModel model : statsUserProblemDao.selectAllByUserJidAndProblemJids(userJid, problemJids)) {
            verdictsMap.put(model.problemJid, model.verdict);
            scoresMap.put(model.problemJid, model.score);
        }

        Map<String, ProblemProgress> progressesMap = new HashMap<>();
        for (String problemJid : problemJids) {
            progressesMap.put(problemJid, new ProblemProgress.Builder()
                    .verdict(verdictsMap.get(problemJid))
                    .score(scoresMap.get(problemJid))
                    .build());
        }
        return ImmutableMap.copyOf(progressesMap);
    }

    public Map<String, ProblemStats> getProblemStatsMap(Set<String> problemJids) {
        Map<String, Long> totalScoresMap = statsUserProblemDao.selectTotalScoresByProblemJids(problemJids);
        Map<String, Long> totalUsersAccepted = statsUserProblemDao.selectCountsAcceptedByProblemJids(problemJids);
        Map<String, Long> totalUsersTried = statsUserProblemDao.selectCountsTriedByProblemJids(problemJids);

        Map<String, ProblemStats> problemStatsMap = new HashMap<>();
        for (String problemJid : problemJids) {
            problemStatsMap.put(problemJid, new ProblemStats.Builder()
                    .totalScores(totalScoresMap.getOrDefault(problemJid, 0L).intValue())
                    .totalUsersAccepted(totalUsersAccepted.getOrDefault(problemJid, 0L).intValue())
                    .totalUsersTried(totalUsersTried.getOrDefault(problemJid, 0L).intValue())
                    .build());
        }
        return ImmutableMap.copyOf(problemStatsMap);
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
        Map<String, Set<String>> problemJidsMap = new HashMap<>();
        Set<String> problemJids = new HashSet<>();
        for (ProblemSetProblemModel m : problemSetProblemDao.selectAllByProblemSetJids(problemSetJids, DEFAULT_ALL)) {
            if (m.type.equals(ProblemType.PROGRAMMING.name())) {
                problemJidsMap.putIfAbsent(m.problemSetJid, new HashSet<>());
                problemJidsMap.get(m.problemSetJid).add(m.problemJid);
                problemJids.add(m.problemJid);
            }
        }

        Map<String, Integer> scoresMap = new HashMap<>();
        for (StatsUserProblemModel m : statsUserProblemDao.selectAllByUserJidAndProblemJids(userJid, problemJids)) {
            scoresMap.put(m.problemJid, m.score);
        }

        Map<String, ProblemSetProgress> progressesMap = new HashMap<>();
        for (String problemSetJid : problemSetJids) {
            int scores = 0;
            for (String problemJid : problemJidsMap.getOrDefault(problemSetJid, emptySet())) {
                scores += scoresMap.getOrDefault(problemJid, 0);
            }
            progressesMap.put(problemSetJid, new ProblemSetProgress.Builder()
                    .score(scores)
                    .totalProblems(problemJidsMap.getOrDefault(problemSetJid, emptySet()).size())
                    .build());
        }
        return ImmutableMap.copyOf(progressesMap);
    }

    public Map<String, Map<String, ProblemProgress>> getUserProblemProgressesMap(Set<String> userJids, Set<String> problemJids) {
        Map<String, Map<String, ProblemProgress>> progressesMap = new HashMap<>();
        for (String userJid : userJids) {
            progressesMap.put(userJid, new HashMap<>());
        }
        for (StatsUserProblemModel m : statsUserProblemDao.selectAllByUserJidsAndProblemJids(userJids, problemJids)) {
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
