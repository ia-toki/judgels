package judgels.sandalphon.persistence;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import judgels.persistence.JudgelsDao;

public interface BaseProgrammingGradingDao<M extends AbstractProgrammingGradingModel> extends JudgelsDao<M> {
    M createGradingModel();
    Class<M> getGradingModelClass();
    Optional<M> selectLatestBySubmissionJid(String submissionJid);
    Map<String, M> selectAllLatestBySubmissionJids(Collection<String> submissionJids);
    Map<String, M> selectAllLatestWithDetailsBySubmissionJids(Collection<String> submissionJids);
    void dump(PrintWriter output, Collection<String> submissionJids);
}
