package judgels.persistence.dao;

import java.util.Collection;
import java.util.Map;
import judgels.persistence.model.ProblemLevelModel;

public interface ProblemLevelDao extends Dao<ProblemLevelModel> {
    Map<String, Integer> selectAllAverageByProblemJids(Collection<String> problemJids);
}
