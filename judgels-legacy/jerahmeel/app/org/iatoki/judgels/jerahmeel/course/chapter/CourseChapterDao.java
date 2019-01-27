package org.iatoki.judgels.jerahmeel.course.chapter;

import com.google.inject.ImplementedBy;
import judgels.persistence.Dao;

@ImplementedBy(CourseChapterHibernateDao.class)
public interface CourseChapterDao extends Dao<CourseChapterModel> {

    boolean existsByCourseJidAndAlias(String courseJid, String alias);

    boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid);
}
