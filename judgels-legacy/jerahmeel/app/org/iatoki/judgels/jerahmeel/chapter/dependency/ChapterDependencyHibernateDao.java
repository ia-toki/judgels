package org.iatoki.judgels.jerahmeel.chapter.dependency;

import judgels.persistence.ActorProvider;
import judgels.persistence.hibernate.HibernateDao;
import org.hibernate.SessionFactory;
import play.db.jpa.JPA;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Clock;
import java.util.List;

@Singleton
public final class ChapterDependencyHibernateDao extends HibernateDao<ChapterDependencyModel> implements ChapterDependencyDao {

    @Inject
    public ChapterDependencyHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByChapterJidAndDependencyJid(String chapterJid, String dependencyJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterDependencyModel> root = query.from(ChapterDependencyModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterDependencyModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterDependencyModel_.dependedChapterJid), dependencyJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public List<ChapterDependencyModel> getByChapterJid(String chapterJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ChapterDependencyModel> query = cb.createQuery(ChapterDependencyModel.class);
        Root<ChapterDependencyModel> root = query.from(ChapterDependencyModel.class);

        query.where(cb.equal(root.get(ChapterDependencyModel_.chapterJid), chapterJid));

        return currentSession().createQuery(query).getResultList();
    }
}
