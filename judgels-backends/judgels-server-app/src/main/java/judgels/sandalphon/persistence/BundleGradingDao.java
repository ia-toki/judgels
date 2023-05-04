package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Map;
import judgels.persistence.JudgelsDao;

public interface BundleGradingDao extends JudgelsDao<BundleGradingModel> {
    List<BundleGradingModel> selectAllBySubmissionJid(String submissionJid);
    Map<String, List<BundleGradingModel>> getBySubmissionJids(List<String> submissionJids);
}
