package org.iatoki.judgels.jerahmeel.chapter.lesson;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class ChapterLessonHibernateDao extends HibernateDao<ChapterLessonModel> implements ChapterLessonDao {

    @Inject
    public ChapterLessonHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByChapterJidAndAlias(String chapterJid, String alias) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ChapterLessonModel> root = query.from(ChapterLessonModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(ChapterLessonModel_.chapterJid), chapterJid), cb.equal(root.get(ChapterLessonModel_.alias), alias)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }
}
