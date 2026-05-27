package judgels.persistence.dao;

import java.util.List;
import java.util.Map;
import judgels.persistence.model.BundleGradingModel;

public interface BundleGradingDao extends JudgelsDao<BundleGradingModel> {
    List<BundleGradingModel> selectAllBySubmissionJid(String submissionJid);
    Map<String, List<BundleGradingModel>> getBySubmissionJids(List<String> submissionJids);
}
