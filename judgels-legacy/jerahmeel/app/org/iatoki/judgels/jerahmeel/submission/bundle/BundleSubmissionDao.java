package org.iatoki.judgels.jerahmeel.submission.bundle;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.BaseBundleSubmissionDao;

@ImplementedBy(BundleSubmissionHibernateDao.class)
public interface BundleSubmissionDao extends BaseBundleSubmissionDao<BundleSubmissionModel> {

}
