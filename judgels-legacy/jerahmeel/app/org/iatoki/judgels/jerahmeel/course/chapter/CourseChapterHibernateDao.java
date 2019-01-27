package org.iatoki.judgels.jerahmeel.course.chapter;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class CourseChapterHibernateDao extends HibernateDao<CourseChapterModel> implements CourseChapterDao {

    @Inject
    public CourseChapterHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByCourseJidAndAlias(String courseJid, String alias) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CourseChapterModel> root = query.from(CourseChapterModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CourseChapterModel_.courseJid), courseJid), cb.equal(root.get(CourseChapterModel_.alias), alias)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CourseChapterModel> root = query.from(CourseChapterModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CourseChapterModel_.courseJid), courseJid), cb.equal(root.get(CourseChapterModel_.chapterJid), chapterJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }
}
