package judgels.persistence;

import java.util.Collection;
import java.util.Map;

public interface ProblemLevelDao extends Dao<ProblemLevelModel> {
    Map<String, Integer> selectAllAverageByProblemJids(Collection<String> problemJids);
}
