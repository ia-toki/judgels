package judgels.jerahmeel.persistence;

import java.util.Map;
import java.util.Set;
import judgels.persistence.Dao;

public interface ProblemLevelDao extends Dao<ProblemLevelModel> {
    Map<String, Integer> selectAllAverageByProblemJids(Set<String> problemJids);
}
