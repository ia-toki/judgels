package org.iatoki.judgels.jerahmeel.chapter.problem;

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
public final class ChapterProblemHibernateDao extends HibernateDao<ChapterProblemModel> implements ChapterProblemDao {

    @Inject
    public ChapterProblemHibernateDao(SessionFactory sessionFactory, Clock clock, ActorProvider actorProvider) {
        super(sessionFactory, clock, actorProvider);
    }

    @Override
    public boolean existsByChapterJidAndAlias(String chapterJid, String alias) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterProblemModel_.alias), alias)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public List<ChapterProblemModel> getByChapterJid(String chapterJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ChapterProblemModel> query = cb.createQuery(ChapterProblemModel.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.where(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid));

        return currentSession().createQuery(query).getResultList();
    }

    @Override
    public ChapterProblemModel findByChapterJidAndProblemJid(String chapterJid, String problemJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<ChapterProblemModel> query = cb.createQuery(ChapterProblemModel.class);
        Root<ChapterProblemModel> root = query.from(ChapterProblemModel.class);

        query.where(cb.and(cb.equal(root.get(ChapterProblemModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterProblemModel_.problemJid), problemJid)));

        return currentSession().createQuery(query).getSingleResult();
    }
}
