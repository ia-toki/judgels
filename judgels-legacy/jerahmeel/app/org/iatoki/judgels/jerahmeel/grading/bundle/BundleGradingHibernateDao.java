package org.iatoki.judgels.jerahmeel.grading.bundle;

import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingHibernateDao;

import javax.inject.Singleton;

@Singleton
public final class BundleGradingHibernateDao extends AbstractBundleGradingHibernateDao<BundleGradingModel> implements BundleGradingDao {

    public BundleGradingHibernateDao() {
        super(BundleGradingModel.class);
    }

    @Override
    public BundleGradingModel createGradingModel() {
        return new BundleGradingModel();
    }
}
