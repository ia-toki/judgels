package judgels.sandalphon.persistence;

import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    QueryBuilder<BundleSubmissionModel> selectByProblemJid(String problemJid);
}
