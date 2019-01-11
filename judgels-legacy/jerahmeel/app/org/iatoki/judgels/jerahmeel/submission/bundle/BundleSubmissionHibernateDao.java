package org.iatoki.judgels.jerahmeel.submission.bundle;

import org.iatoki.judgels.sandalphon.problem.bundle.submission.AbstractBundleSubmissionHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class BundleSubmissionHibernateDao extends AbstractBundleSubmissionHibernateDao<BundleSubmissionModel> implements BundleSubmissionDao {

    public BundleSubmissionHibernateDao() {
        super(BundleSubmissionModel.class);
    }

    @Override
    public BundleSubmissionModel createSubmissionModel() {
        return new BundleSubmissionModel();
    }
}
