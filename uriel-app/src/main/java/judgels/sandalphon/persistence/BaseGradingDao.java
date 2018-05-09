package judgels.sandalphon.persistence;

import java.util.Map;
import java.util.Set;
import judgels.persistence.JudgelsDao;

public interface BaseGradingDao<M extends AbstractGradingModel> extends JudgelsDao<M> {
    M createGradingModel();
    Class<M> getGradingModelClass();
    Map<String, M> selectAllLatestBySubmissionJids(Set<String> submissionJids);
}
