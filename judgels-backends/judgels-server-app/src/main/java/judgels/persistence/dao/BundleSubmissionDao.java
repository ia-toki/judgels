package judgels.persistence.dao;

import judgels.persistence.QueryBuilder;
import judgels.persistence.model.BundleSubmissionModel;

public interface BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    QueryBuilder<BundleSubmissionModel> selectByProblemJid(String problemJid);
}
