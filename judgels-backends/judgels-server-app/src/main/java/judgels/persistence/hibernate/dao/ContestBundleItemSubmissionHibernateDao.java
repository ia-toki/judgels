package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.ContestBundleItemSubmissionDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.ContestBundleItemSubmissionModel;

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
