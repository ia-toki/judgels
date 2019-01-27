package org.iatoki.judgels.jerahmeel.curriculum.course;

import judgels.persistence.hibernate.HibernateDao;
import judgels.persistence.hibernate.HibernateDaoData;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Singleton
public final class CurriculumCourseHibernateDao extends HibernateDao<CurriculumCourseModel> implements CurriculumCourseDao {

    @Inject
    public CurriculumCourseHibernateDao(HibernateDaoData data) {
        super(data);
    }

    @Override
    public boolean existsByCurriculumJidAndAlias(String curriculumJid, String alias) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CurriculumCourseModel> root = query.from(CurriculumCourseModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CurriculumCourseModel_.curriculumJid), curriculumJid), cb.equal(root.get(CurriculumCourseModel_.alias), alias)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }

    @Override
    public boolean existsByCurriculumJidAndCourseJid(String curriculumJid, String courseJid) {
        CriteriaBuilder cb = currentSession().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<CurriculumCourseModel> root = query.from(CurriculumCourseModel.class);

        query.select(cb.count(root)).where(cb.and(cb.equal(root.get(CurriculumCourseModel_.curriculumJid), curriculumJid), cb.equal(root.get(CurriculumCourseModel_.courseJid), courseJid)));

        return (currentSession().createQuery(query).getSingleResult() != 0);
    }
}
