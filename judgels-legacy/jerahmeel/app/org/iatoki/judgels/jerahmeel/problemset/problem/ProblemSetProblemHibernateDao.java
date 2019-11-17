package org.iatoki.judgels.jerahmeel.problemset.problem;

import judgels.jerahmeel.persistence.ProblemSetProblemModel;
import judgels.jerahmeel.persistence.ProblemSetProblemModel_;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public final class ProblemSetProblemHibernateDao extends HibernateDao<ProblemSetProblemModel> implements ProblemSetProblemDao {

    @Inject
    public ProblemSetProblemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByProblemSetJidAndAlias(String problemSetJid, String alias) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ProblemSetProblemModel> root = query.from(ProblemSetProblemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ProblemSetProblemModel_.problemSetJid), problemSetJid), cb.equal(root.get(ProblemSetProblemModel_.alias), alias)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public List<ProblemSetProblemModel> getByProblemSetJid(String problemSetJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ProblemSetProblemModel> query = cb.createQuery(ProblemSetProblemModel.class);
        Root<ProblemSetProblemModel> root = query.from(ProblemSetProblemModel.class);

        query.where(cb.equal(root.get(ProblemSetProblemModel_.problemSetJid), problemSetJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public ProblemSetProblemModel findByProblemSetJidAndProblemJid(String problemSetJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ProblemSetProblemModel> query = cb.createQuery(ProblemSetProblemModel.class);
        Root<ProblemSetProblemModel> root = query.from(ProblemSetProblemModel.class);

        query.where(cb.and(cb.equal(root.get(ProblemSetProblemModel_.problemSetJid), problemSetJid), cb.equal(root.get(ProblemSetProblemModel_.problemJid), problemJid)));

        return currentSession().createQuery(query).getSingleResult();
    }
}
