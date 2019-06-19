package judgels.sandalphon.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;

public interface BaseProgrammingGradingDao<M extends AbstractProgrammingGradingModel> extends JudgelsDao<M> {
    M createGradingModel();
    Class<M> getGradingModelClass();
    Optional<M> selectLatestBySubmissionJid(String submissionJid);
    Map<String, M> selectAllLatestBySubmissionJids(Set<String> submissionJids);
    Map<String, M> selectAllLatestWithDetailsBySubmissionJids(Set<String> submissionJids);
}
