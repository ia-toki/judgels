package judgels.sandalphon.persistence;

import java.time.Instant;
import java.util.List;
import judgels.persistence.JudgelsDao;

public interface BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    List<Instant> getAllSubmissionsSubmitTime();
}
