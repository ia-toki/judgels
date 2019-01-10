package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import com.google.inject.ImplementedBy;

@ImplementedBy(BundleSubmissionHibernateDao.class)
public interface BundleSubmissionDao extends BaseBundleSubmissionDao<BundleSubmissionModel> {

}
