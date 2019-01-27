package org.iatoki.judgels.jerahmeel.problemset.problem;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;
import play.db.jpa.JPA;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.util.List;

@Singleton
public final class ProblemSetProblemHibernateDao extends HibernateDao<ProblemSetProblemModel> implements ProblemSetProblemDao {

    @Inject
    public ProblemSetProblemHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
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
