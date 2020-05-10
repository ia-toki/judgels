package org.iatoki.judgels.sandalphon.problem.bundle.grading;

import com.google.inject.ImplementedBy;

@ImplementedBy(BundleGradingHibernateDao.class)
public interface BundleGradingDao extends BaseBundleGradingDao<BundleGradingModel> {

}
