package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.ContestBundleItemSubmissionDao;
import judgels.persistence.ContestBundleItemSubmissionModel;
import judgels.persistence.hibernate.HibernateDaoData;

public class ContestBundleItemSubmissionHibernateDao
        extends AbstractBundleItemSubmissionHibernateDao<ContestBundleItemSubmissionModel>
        implements ContestBundleItemSubmissionDao {

    @Inject
    public ContestBundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public ContestBundleItemSubmissionModel createSubmissionModel() {
        return new ContestBundleItemSubmissionModel();
    }
}
