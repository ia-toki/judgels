package org.iatoki.judgels.jerahmeel.chapter.lesson;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class ChapterLessonHibernateDao extends AbstractHibernateDao<Long, ChapterLessonModel> implements ChapterLessonDao {

    public ChapterLessonHibernateDao() {
        super(ChapterLessonModel.class);
    }

    @Override
    public boolean existsByChapterJidAndAlias(String chapterJid, String alias) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterLessonModel> root = query.from(ChapterLessonModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterLessonModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterLessonModel_.alias), alias)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }
}
