package judgels.persistence.hibernate.dao;

import jakarta.inject.Inject;
import judgels.persistence.QueryBuilder;
import judgels.persistence.dao.BundleSubmissionDao;
import judgels.persistence.model.BundleSubmissionModel;
import judgels.persistence.model.BundleSubmissionModel_;

public final class BundleSubmissionHibernateDao extends JudgelsHibernateDao<BundleSubmissionModel> implements BundleSubmissionDao {
    @Inject
    public BundleSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public QueryBuilder<BundleSubmissionModel> selectByProblemJid(String problemJid) {
        return select()
                .where(columnEq(BundleSubmissionModel_.problemJid, problemJid));
    }
}
