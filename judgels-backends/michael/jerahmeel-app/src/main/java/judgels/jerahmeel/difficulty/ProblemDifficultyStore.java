package judgels.jerahmeel.difficulty;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.problem.ProblemDifficulty;
import judgels.jerahmeel.api.problem.ProblemStats;
import judgels.jerahmeel.persistence.ProblemLevelDao;
import judgels.jerahmeel.stats.StatsStore;

public class ProblemDifficultyStore {
    private final StatsStore statsStore;
    private final ProblemLevelDao problemLevelDao;

    @Inject
    public ProblemDifficultyStore(StatsStore statsStore, ProblemLevelDao problemLevelDao) {
        this.statsStore = statsStore;
        this.problemLevelDao = problemLevelDao;
    }

    public Map<String, ProblemDifficulty> getProblemDifficultiesMap(Set<String> problemJids) {
        Map<String, ProblemStats> statsMap = statsStore.getProblemStatsMap(problemJids);
        Map<String, Integer> levelsMap = problemLevelDao.selectAllAverageByProblemJids(problemJids);

        return problemJids.stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ProblemDifficulty.Builder()
                        .stats(statsMap.get(jid))
                        .level(Optional.ofNullable(levelsMap.get(jid)))
                        .build()));
    }
}
