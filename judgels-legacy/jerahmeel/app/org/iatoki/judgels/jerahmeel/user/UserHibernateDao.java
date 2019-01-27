package org.iatoki.judgels.jerahmeel.user;

import com.google.common.collect.ImmutableList;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.time.Clock;
import java.util.List;

@Singleton
public final class UserHibernateDao extends HibernateDao<UserModel> implements UserDao {

    @Inject
    public UserHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<UserModel> root = query.from(UserModel.class);

        query
            .select(cb.count(root))
            .where(cb.equal(root.get(UserModel_.userJid), jid));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public UserModel findByJid(String jid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> query = cb.createQuery(UserModel.class);
        Root<UserModel> root = query.from(UserModel.class);

        query.where(cb.equal(root.get(UserModel_.userJid), jid));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    protected List<SingularAttribute<UserModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(UserModel_.roles);
    }
}
