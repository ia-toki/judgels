package org.iatoki.judgels.sandalphon.lesson.partner;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.util.List;

@Singleton
public final class LessonPartnerHibernateDao extends HibernateDao<LessonPartnerModel> implements LessonPartnerDao {

    @Inject
    public LessonPartnerHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .select(cb.count(root))
                .where(cb.and(cb.equal(root.get(LessonPartnerModel_.lessonJid), lessonJid), cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public LessonPartnerModel findByLessonJidAndPartnerJid(String lessonJid, String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<LessonPartnerModel> query = cb.createQuery(getEntityClass());
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .where(cb.and(cb.equal(root.get(LessonPartnerModel_.lessonJid), lessonJid), cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid)));

        return currentSession().createQuery(query).getSingleResult();
    }

    @Override
    public List<String> getLessonJidsByPartnerJid(String partnerJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<LessonPartnerModel> root = query.from(getEntityClass());

        query
                .select(root.get(LessonPartnerModel_.lessonJid))
                .where(cb.equal(root.get(LessonPartnerModel_.userJid), partnerJid));

        return currentSession().createQuery(query).getResultList();
    }
}
