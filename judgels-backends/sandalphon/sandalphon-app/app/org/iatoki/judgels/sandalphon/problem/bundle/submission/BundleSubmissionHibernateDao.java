package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

@Singleton
public final class BundleSubmissionHibernateDao extends JudgelsHibernateDao<BundleSubmissionModel> implements BundleSubmissionDao {

    @Inject
    public BundleSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<Instant> getAllSubmissionsSubmitTime() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Instant> query = cb.createQuery(Instant.class);
        Root<BundleSubmissionModel> root = query.from(getEntityClass());

        query.select(root.get(JudgelsModel_.createdAt));

        return currentSession().createQuery(query).getResultList();
    }
}
