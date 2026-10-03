package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.model.TrainingBundleItemSubmissionModel;
import org.hibernate.query.Query;

public class TrainingBundleItemSubmissionDao
        extends BaseBundleItemSubmissionDao<TrainingBundleItemSubmissionModel> {

    @Inject
    public TrainingBundleItemSubmissionDao(DaoData data) {
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
