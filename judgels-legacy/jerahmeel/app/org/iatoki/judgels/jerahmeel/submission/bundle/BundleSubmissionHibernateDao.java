package org.iatoki.judgels.jerahmeel.submission.bundle;

import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.sandalphon.problem.bundle.submission.AbstractBundleSubmissionHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BundleSubmissionHibernateDao extends AbstractBundleSubmissionHibernateDao<BundleSubmissionModel> implements BundleSubmissionDao {

    @Inject
    public BundleSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public BundleSubmissionModel createSubmissionModel() {
        return new BundleSubmissionModel();
    }
}
