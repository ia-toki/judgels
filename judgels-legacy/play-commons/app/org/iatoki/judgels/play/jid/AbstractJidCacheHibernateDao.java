package org.iatoki.judgels.play.jid;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

public abstract class AbstractJidCacheHibernateDao<M extends AbstractJidCacheModel> extends AbstractHibernateDao<Long, M> implements BaseJidCacheDao<M> {

    protected AbstractJidCacheHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public final boolean existsByJid(String jid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(AbstractJidCacheModel_.jid), jid));

        return JPA.em().createQuery(query).getSingleResult() > 0;
    }

    @Override
    public final M findByJid(String jid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        Root<M> root = query.from(getModelClass());

        query.where(cb.equal(root.get(AbstractJidCacheModel_.jid), jid));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public final List<M> getByJids(Collection<String> jids) {
        if (jids.isEmpty()) {
            return ImmutableList.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        Root<M> root = query.from(getModelClass());

        query.where(root.get(AbstractJidCacheModel_.jid).in(jids));

        return JPA.em().createQuery(query).getResultList();
    }
}
