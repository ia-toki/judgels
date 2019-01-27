package org.iatoki.judgels.jerahmeel.scorecache;

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

@Singleton
public final class ContainerProblemScoreCacheHibernateDao extends HibernateDao<ContainerProblemScoreCacheModel> implements ContainerProblemScoreCacheDao {

    @Inject
    public ContainerProblemScoreCacheHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ContainerProblemScoreCacheModel> root = query.from(ContainerProblemScoreCacheModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ContainerProblemScoreCacheModel_.userJid), userJid), cb.equal(root.get(ContainerProblemScoreCacheModel_.containerJid), containerJid), cb.equal(root.get(ContainerProblemScoreCacheModel_.problemJid), problemJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public ContainerProblemScoreCacheModel getByUserJidContainerJidAndProblemJid(String userJid, String containerJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContainerProblemScoreCacheModel> query = cb.createQuery(ContainerProblemScoreCacheModel.class);
        Root<ContainerProblemScoreCacheModel> root = query.from(ContainerProblemScoreCacheModel.class);

        query.where(cb.and(cb.equal(root.get(ContainerProblemScoreCacheModel_.userJid), userJid), cb.equal(root.get(ContainerProblemScoreCacheModel_.containerJid), containerJid), cb.equal(root.get(ContainerProblemScoreCacheModel_.problemJid), problemJid)));

        return getFirstResultAndDeleteTheRest(query);
    }
}
