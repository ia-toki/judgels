package judgels.sandalphon.hibernate;

import javax.inject.Inject;
import judgels.persistence.QueryBuilder;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.BundleSubmissionDao;
import judgels.sandalphon.persistence.BundleSubmissionModel;
import judgels.sandalphon.persistence.BundleSubmissionModel_;

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
