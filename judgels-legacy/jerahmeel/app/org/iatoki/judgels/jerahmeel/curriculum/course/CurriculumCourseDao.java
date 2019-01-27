package org.iatoki.judgels.jerahmeel.curriculum.course;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(CurriculumCourseHibernateDao.class)
public interface CurriculumCourseDao extends Dao<CurriculumCourseModel> {

    boolean existsByCurriculumJidAndAlias(String curriculumJid, String alias);

    boolean existsByCurriculumJidAndCourseJid(String curriculumJid, String courseJid);
}
