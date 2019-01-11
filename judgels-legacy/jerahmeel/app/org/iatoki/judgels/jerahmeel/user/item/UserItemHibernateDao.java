package org.iatoki.judgels.jerahmeel.user.item;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public final class UserItemHibernateDao extends AbstractHibernateDao<Long, UserItemModel> implements UserItemDao {

    public UserItemHibernateDao() {
        super(UserItemModel.class);
    }

    @Override
    public boolean existsByUserJidAndItemJid(String userJid, String itemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid)));

        return JPA.em().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public boolean existsByUserJidItemJidAndStatus(String userJid, String itemJid, String status) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid), cb.equal(root.get(UserItemModel_.status), status)));

        return JPA.em().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public UserItemModel findByUserJidAndItemJid(String userJid, String itemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid)));

        return getFirstResultAndDeleteTheRest(query);
    }

    @Override
    public List<UserItemModel> getByUserJid(String userJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.equal(root.get(UserItemModel_.userJid), userJid));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public List<UserItemModel> getByItemJid(String itemJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.equal(root.get(UserItemModel_.itemJid), itemJid));

        return JPA.em().createQuery(query).getResultList();
    }

    @Override
    public List<UserItemModel> getByUserJidAndStatus(String userJid, String status) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.status), status)));

        return JPA.em().createQuery(query).getResultList();
    }
}
