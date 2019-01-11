package org.iatoki.judgels.jerahmeel.grading.bundle;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.BaseBundleGradingDao;

@ImplementedBy(BundleGradingHibernateDao.class)
public interface BundleGradingDao extends BaseBundleGradingDao<BundleGradingModel> {

}
