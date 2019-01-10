package org.iatoki.judgels.jophiel.avatar;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractAvatarCacheHibernateDao<M extends AbstractAvatarCacheModel> extends AbstractHibernateDao<Long, M> implements BaseAvatarCacheDao<M> {

    protected AbstractAvatarCacheHibernateDao(Class<M> modelClass) {
        super(modelClass);
    }

    @Override
    public boolean existsByUserJid(String userJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<M> root = query.from(getModelClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(AbstractAvatarCacheModel_.userJid), userJid));

        return JPA.em().createQuery(query).getSingleResult() > 0;
    }

    @Override
    public M findByUserJid(String userJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        Root<M> root = query.from(getModelClass());

        query.where(cb.equal(root.get(AbstractAvatarCacheModel_.userJid), userJid));

        return JPA.em().createQuery(query).getSingleResult();
    }

    @Override
    public List<M> findByUserJids(List<String> userJids) {
        if (userJids.isEmpty()) {
            return ImmutableList.of();
        }

        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<M> query = cb.createQuery(getModelClass());

        Root<M> root = query.from(getModelClass());

        query.where(root.get(AbstractAvatarCacheModel_.userJid).in(userJids));

        return JPA.em().createQuery(query).getResultList();
    }
}
