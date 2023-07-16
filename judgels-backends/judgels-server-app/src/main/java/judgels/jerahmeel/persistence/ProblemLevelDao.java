package judgels.jerahmeel.persistence;

import java.util.Collection;
import java.util.Map;
import judgels.persistence.Dao;

public interface ProblemLevelDao extends Dao<ProblemLevelModel> {
    Map<String, Integer> selectAllAverageByProblemJids(Collection<String> problemJids);
}
