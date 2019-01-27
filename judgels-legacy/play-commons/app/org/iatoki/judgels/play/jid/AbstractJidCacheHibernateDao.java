package org.iatoki.judgels.play.jid;

import com.google.common.collect.ImmutableList;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.util.Collection;
import java.util.List;

public abstract class AbstractJidCacheHibernateDao<M extends AbstractJidCacheModel> extends HibernateDao<M> implements BaseJidCacheDao<M> {

    public AbstractJidCacheHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public final boolean existsByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(AbstractJidCacheModel_.jid), jid));

        return currentSession().createQuery(query).getSingleResult() > 0;
    }

    @Override
    public final M findByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());

        Root<M> root = query.from(getEntityClass());

        query.where(cb.equal(root.get(AbstractJidCacheModel_.jid), jid));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public final List<M> getByJids(Collection<String> jids) {
        if (jids.isEmpty()) {
            return ImmutableList.of();
        }

        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getEntityClass());

        Root<M> root = query.from(getEntityClass());

        query.where(root.get(AbstractJidCacheModel_.jid).in(jids));

        return currentSession().createQuery(query).getResultList();
    }
}
