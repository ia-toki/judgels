package org.iatoki.judgels.sandalphon.problem.programming.submission;

import judgels.persistence.JudgelsModel_;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.List;

public abstract class AbstractProgrammingSubmissionHibernateDao<M extends AbstractProgrammingSubmissionModel> extends JudgelsHibernateDao<M> implements BaseProgrammingSubmissionDao<M> {

    public AbstractProgrammingSubmissionHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<M> getByContainerJidSinceTime(String containerJid, long time) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());
        Root<M> root = query.from(getEntityClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.lessThanOrEqualTo(root.get(AbstractProgrammingSubmissionModel_.createdAt), Instant.ofEpochMilli(time))));
        query.orderBy(cb.asc(root.get(AbstractProgrammingSubmissionModel_.createdAt)));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public List<M> getByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());
        Root<M> root = query.from(getEntityClass());

        query.where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.createdBy), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));
        query.orderBy(cb.asc(root.get(AbstractProgrammingSubmissionModel_.createdAt)));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public long countByContainerJidAndUserJidAndProblemJid(String containerJid, String userJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getEntityClass());

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(AbstractProgrammingSubmissionModel_.containerJid), containerJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.createdBy), userJid), cb.equal(root.get(AbstractProgrammingSubmissionModel_.problemJid), problemJid)));

        return currentSession().createQuery(query).getSingleResult();
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
