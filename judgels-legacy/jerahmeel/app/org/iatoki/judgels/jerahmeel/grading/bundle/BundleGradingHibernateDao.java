package org.iatoki.judgels.jerahmeel.grading.bundle;

import judgels.persistence.hibernate.HibernateDaoData;
import org.iatoki.judgels.sandalphon.problem.bundle.grading.AbstractBundleGradingHibernateDao;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class BundleGradingHibernateDao extends AbstractBundleGradingHibernateDao<BundleGradingModel> implements BundleGradingDao {

    @Inject
    public BundleGradingHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public BundleGradingModel createGradingModel() {
        return new BundleGradingModel();
    }
}
