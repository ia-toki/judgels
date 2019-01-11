package org.iatoki.judgels.jerahmeel.course.chapter;

import org.iatoki.judgels.play.model.AbstractHibernateDao;
import play.db.jpa.JPA;

import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class CourseChapterHibernateDao extends AbstractHibernateDao<Long, CourseChapterModel> implements CourseChapterDao {

    public CourseChapterHibernateDao() {
        super(CourseChapterModel.class);
    }

    @Override
    public boolean existsByCourseJidAndAlias(String courseJid, String alias) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CourseChapterModel> root = query.from(CourseChapterModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CourseChapterModel_.courseJid), courseJid), cb.equal(root.get(CourseChapterModel_.alias), alias)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CourseChapterModel> root = query.from(CourseChapterModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CourseChapterModel_.courseJid), courseJid), cb.equal(root.get(CourseChapterModel_.chapterJid), chapterJid)));

        return (JPA.em().createQuery(query).getSingleResult() != 0);
    }
}
