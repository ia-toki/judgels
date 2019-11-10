package judgels.jerahmeel.hibernate;

import javax.inject.Inject;
import javax.inject.Singleton;
import judgels.jerahmeel.persistence.BundleItemSubmissionDao;
import judgels.jerahmeel.persistence.BundleItemSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractBundleItemSubmissionHibernateDao;

@Singleton
public class BundleItemSubmissionHibernateDao
        extends AbstractBundleItemSubmissionHibernateDao<BundleItemSubmissionModel>
        implements BundleItemSubmissionDao {

    @Inject
    public BundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public BundleItemSubmissionModel createSubmissionModel() {
        return new BundleItemSubmissionModel();
    }
}
