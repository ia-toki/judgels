package judgels.uriel.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.sandalphon.hibernate.AbstractBundleItemSubmissionHibernateDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionDao;
import judgels.uriel.persistence.ContestBundleItemSubmissionModel;

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
