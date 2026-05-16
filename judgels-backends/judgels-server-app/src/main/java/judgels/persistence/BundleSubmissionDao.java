package judgels.persistence;


public interface BundleSubmissionDao extends JudgelsDao<BundleSubmissionModel> {
    QueryBuilder<BundleSubmissionModel> selectByProblemJid(String problemJid);
}
