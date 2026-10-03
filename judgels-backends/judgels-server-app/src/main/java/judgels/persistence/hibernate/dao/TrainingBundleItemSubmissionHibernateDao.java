package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.dao.TrainingBundleItemSubmissionDao;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.model.TrainingBundleItemSubmissionModel;
import org.hibernate.query.Query;

public class TrainingBundleItemSubmissionHibernateDao
        extends AbstractBundleItemSubmissionHibernateDao<TrainingBundleItemSubmissionModel>
        implements TrainingBundleItemSubmissionDao {

    @Inject
    public TrainingBundleItemSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public TrainingBundleItemSubmissionModel createSubmissionModel() {
        return new TrainingBundleItemSubmissionModel();
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
