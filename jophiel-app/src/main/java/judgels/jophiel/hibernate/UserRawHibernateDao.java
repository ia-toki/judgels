package judgels.jophiel.hibernate;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.jophiel.persistence.UserModel;
import judgels.jophiel.persistence.UserModel_;
import judgels.jophiel.persistence.UserRawDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@Singleton
public class UserRawHibernateDao implements UserRawDao {
    private final SessionFactory sessionFactory;

    @Inject
    public UserRawHibernateDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<UserModel> selectByTerm(String term) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<UserModel> cq = cb.createQuery(UserModel.class);
        Root<UserModel> root = cq.from(UserModel.class);
        cq.where(cb.like(root.get(UserModel_.username), "%" + term + "%"));
        return currentSession().createQuery(cq).getResultList();
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}
