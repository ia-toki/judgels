package org.iatoki.judgels.jerahmeel.curriculum.course;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(CurriculumCourseHibernateDao.class)
public interface CurriculumCourseDao extends Dao<Long, CurriculumCourseModel> {

    boolean existsByCurriculumJidAndAlias(String curriculumJid, String alias);

    boolean existsByCurriculumJidAndCourseJid(String curriculumJid, String courseJid);
}
