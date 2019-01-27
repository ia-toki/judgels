package org.iatoki.judgels.jerahmeel.scorecache;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class ContainerScoreCacheHibernateDao extends HibernateDao<ContainerScoreCacheModel> implements ContainerScoreCacheDao {

    @Inject
    public ContainerScoreCacheHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByUserJidAndContainerJid(String userJid, String containerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ContainerScoreCacheModel> root = query.from(ContainerScoreCacheModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ContainerScoreCacheModel_.userJid), userJid), cb.equal(root.get(ContainerScoreCacheModel_.containerJid), containerJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public ContainerScoreCacheModel getByUserJidAndContainerJid(String userJid, String containerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ContainerScoreCacheModel> query = cb.createQuery(ContainerScoreCacheModel.class);
        Root<ContainerScoreCacheModel> root = query.from(ContainerScoreCacheModel.class);

        query.where(cb.and(cb.equal(root.get(ContainerScoreCacheModel_.userJid), userJid), cb.equal(root.get(ContainerScoreCacheModel_.containerJid), containerJid)));

        return getFirstResultAndDeleteTheRest(query);
    }
}
