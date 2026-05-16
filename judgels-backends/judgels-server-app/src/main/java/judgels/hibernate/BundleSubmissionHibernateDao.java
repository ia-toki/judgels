package judgels.hibernate;

import jakarta.inject.Inject;
import judgels.persistence.BundleSubmissionDao;
import judgels.persistence.BundleSubmissionModel;
import judgels.persistence.BundleSubmissionModel_;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

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
