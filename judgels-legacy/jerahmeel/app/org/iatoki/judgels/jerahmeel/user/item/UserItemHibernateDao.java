package org.iatoki.judgels.jerahmeel.user.item;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public final class UserItemHibernateDao extends HibernateDao<UserItemModel> implements UserItemDao {

    @Inject
    public UserItemHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByUserJidAndItemJid(String userJid, String itemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid)));

        return currentSession().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public boolean existsByUserJidItemJidAndStatus(String userJid, String itemJid, String status) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid), cb.equal(root.get(UserItemModel_.status), status)));

        return currentSession().createQuery(query).getSingleResult() != 0;
    }

    @Override
    public UserItemModel findByUserJidAndItemJid(String userJid, String itemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.itemJid), itemJid)));

        return getFirstResultAndDeleteTheRest(query);
    }

    @Override
    public List<UserItemModel> getByUserJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.equal(root.get(UserItemModel_.userJid), userJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public List<UserItemModel> getByItemJid(String itemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.equal(root.get(UserItemModel_.itemJid), itemJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public List<UserItemModel> getByUserJidAndStatus(String userJid, String status) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserItemModel> query = cb.createQuery(UserItemModel.class);
        Root<UserItemModel> root = query.from(UserItemModel.class);

        query.where(cb.and(cb.equal(root.get(UserItemModel_.userJid), userJid), cb.equal(root.get(UserItemModel_.status), status)));

        return currentSession().createQuery(query).getResultList();
    }
}
