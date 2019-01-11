package org.iatoki.judgels.jerahmeel.chapter.dependency;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public final class ChapterDependencyHibernateDao extends AbstractHibernateDao<Long, ChapterDependencyModel> implements ChapterDependencyDao {

    public ChapterDependencyHibernateDao() {
        super(ChapterDependencyModel.class);
    }

    @Override
    public boolean existsByChapterJidAndDependencyJid(String chapterJid, String dependencyJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterDependencyModel> root = query.from(ChapterDependencyModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterDependencyModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterDependencyModel_.dependedChapterJid), dependencyJid)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public List<ChapterDependencyModel> getByChapterJid(String chapterJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<ChapterDependencyModel> query = cb.createQuery(ChapterDependencyModel.class);
        Root<ChapterDependencyModel> root = query.from(ChapterDependencyModel.class);

        query.where(cb.equal(root.get(ChapterDependencyModel_.chapterJid), chapterJid));

        return JPA.em().createQuery(query).getResultList();
    }
}
