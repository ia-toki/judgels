package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.inject.ImplementedBy;
import java.time.Instant;
import java.util.List;
import judgels.persistence.JudgelsDao;

@ImplementedBy(BundleSubmissionHibernateDao.class)
public interface BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    List<Instant> getAllSubmissionsSubmitTime();
}
