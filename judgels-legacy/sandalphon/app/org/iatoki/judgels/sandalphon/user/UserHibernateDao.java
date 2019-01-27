package org.iatoki.judgels.sandalphon.user;

import com.google.common.collect.ImmutableList;
import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

@Singleton
public final class UserHibernateDao extends HibernateDao<UserModel> implements UserDao {

    @Inject
    public UserHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserModel> root = query.from(UserModel.class);

        query
            .select(cb.count(root))
            .where(cb.equal(root.get(UserModel_.userJid), userJid));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public UserModel findByJid(String userJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> query = cb.createQuery(UserModel.class);
        Root<UserModel> root = query.from(UserModel.class);

        query.where(cb.equal(root.get(UserModel_.userJid), userJid));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    protected List<SingularAttribute<UserModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(UserModel_.roles);
    }
}
