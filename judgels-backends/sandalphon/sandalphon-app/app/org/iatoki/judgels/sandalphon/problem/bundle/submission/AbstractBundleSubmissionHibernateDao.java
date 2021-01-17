package org.iatoki.judgels.sandalphon.problem.bundle.submission;

import java.time.Instant;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.JudgelsModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

public abstract class AbstractBundleSubmissionHibernateDao<M extends AbstractBundleSubmissionModel> extends JudgelsHibernateDao<M> implements BaseBundleSubmissionDao<M> {

    public AbstractBundleSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());
        Root<M> root = query.from(getEntityClass());

        query.where(cb.and(cb.equal(root.get(AbstractBundleSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractBundleSubmissionModel_.createdBy), userJid), cb.equal(root.get(AbstractBundleSubmissionModel_.problemJid), problemJid)));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public List<Instant> getAllSubmissionsSubmitTime() {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Instant> query = cb.createQuery(Instant.class);
        Root<M> root = query.from(getEntityClass());

        query.select(root.get(JudgelsModel_.createdAt));

        return currentSession().createQuery(query).getResultList();
    }
}
