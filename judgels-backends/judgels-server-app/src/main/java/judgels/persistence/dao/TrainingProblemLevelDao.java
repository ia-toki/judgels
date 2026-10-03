package judgels.persistence.dao;

import java.util.Collection;
import java.util.Map;
import judgels.persistence.Dao;
import judgels.persistence.model.TrainingProblemLevelModel;

public interface TrainingProblemLevelDao extends Dao<TrainingProblemLevelModel> {
    Map<String, Integer> selectAllAverageByProblemJids(Collection<String> problemJids);
}
