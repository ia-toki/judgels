package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.BundleItemSubmissionDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.BundleItemSubmissionModel;
import org.hibernate.query.Query;

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

    @Override
    public void deleteAllByProblemJid(String problemJid) {
        Query<?> query = currentSession().createQuery(
                "DELETE FROM jerahmeel_bundle_item_submission "
                        + "WHERE problemJid = :problemJid");

        query.setParameter("problemJid", problemJid);
        query.executeUpdate();
    }
}
