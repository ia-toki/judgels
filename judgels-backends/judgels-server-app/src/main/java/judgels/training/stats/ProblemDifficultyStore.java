package judgels.training.stats;

import jakarta.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import judgels.api.training.stats.ProblemDifficulty;
import judgels.api.training.stats.ProblemStats;
import judgels.persistence.dao.TrainingProblemLevelDao;

public class ProblemDifficultyStore {
    private final StatsStore statsStore;
    private final TrainingProblemLevelDao problemLevelDao;

    @Inject
    public ProblemDifficultyStore(StatsStore statsStore, TrainingProblemLevelDao problemLevelDao) {
        this.statsStore = statsStore;
        this.problemLevelDao = problemLevelDao;
    }

    public Map<String, ProblemDifficulty> getProblemDifficultiesMap(Collection<String> problemJids) {
        Map<String, ProblemStats> statsMap = statsStore.getProblemStatsMap(problemJids);
        Map<String, Integer> levelsMap = problemLevelDao.selectAllAverageByProblemJids(problemJids);

        return Set.copyOf(problemJids).stream().collect(Collectors.toMap(
                Function.identity(),
                jid -> new ProblemDifficulty.Builder()
                        .stats(statsMap.get(jid))
                        .level(Optional.ofNullable(levelsMap.get(jid)))
                        .build()));
    }
}
