package org.iatoki.judgels.sandalphon.lesson;

import com.google.common.collect.ImmutableList;
import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.JudgelsHibernateDao;
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
public final class LessonHibernateDao extends JudgelsHibernateDao<LessonModel> implements LessonDao {

    @Inject
    public LessonHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
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

    @Override
    protected List<SingularAttribute<LessonModel, String>> getColumnsFilterableByString() {
        return ImmutableList.of(LessonModel_.slug, LessonModel_.additionalNote);
    }
}
