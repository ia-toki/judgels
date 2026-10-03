package judgels.persistence.dao;

import jakarta.inject.Inject;
import judgels.persistence.DaoData;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.BundleSubmissionModel;
import judgels.persistence.model.BundleSubmissionModel_;

public final class BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    @Inject
    public BundleSubmissionDao(DaoData data) {
        super(data);
    }

    public QueryBuilder<BundleSubmissionModel> selectByProblemJid(String problemJid) {
        return select()
                .where(columnEq(BundleSubmissionModel_.problemJid, problemJid));
    }
}
