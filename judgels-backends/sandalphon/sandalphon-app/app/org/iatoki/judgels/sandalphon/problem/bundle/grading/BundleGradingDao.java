package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.inject.ImplementedBy;
import java.util.List;
import java.util.Map;
import judgels.persistence.JudgelsDao;

@ImplementedBy(BundleGradingHibernateDao.class)
public interface BundleGradingDao extends JudgelsDao<BundleGradingModel> {
    Map<String, List<BundleGradingModel>> getBySubmissionJids(List<String> submissionJids);
}
