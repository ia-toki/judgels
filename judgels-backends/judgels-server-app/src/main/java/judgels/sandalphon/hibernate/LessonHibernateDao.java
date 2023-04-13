package judgels.sandalphon.hibernate;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import judgels.persistence.hibernate.HibernateDaoData;
import judgels.persistence.hibernate.JudgelsHibernateDao;
import judgels.sandalphon.persistence.LessonDao;
import judgels.sandalphon.persistence.LessonModel;
import judgels.sandalphon.persistence.LessonModel_;

@Singleton
public final class LessonHibernateDao extends JudgelsHibernateDao<LessonModel> implements LessonDao {

    @Inject
    public LessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public List<String> getJidsByAuthorJid(String authorJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<LessonModel> root = query.from(getEntityClass());

        query
                .select(root.get(LessonModel_.jid))
                .where(cb.equal(root.get(LessonModel_.createdBy), authorJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public LessonModel findBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<LessonModel> query = cb.createQuery(LessonModel.class);
        Root<LessonModel> root = query.from(getEntityClass());

        query.where(cb.equal(root.get(LessonModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public boolean existsBySlug(String slug) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<LessonModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.equal(root.get(LessonModel_.slug), slug));

        return currentSession().createQuery(query).getSingleResult() > 0;
    }
}
