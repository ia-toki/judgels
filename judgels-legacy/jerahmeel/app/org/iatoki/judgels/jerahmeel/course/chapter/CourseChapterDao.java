package org.iatoki.judgels.jerahmeel.course.chapter;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(CourseChapterHibernateDao.class)
public interface CourseChapterDao extends Dao<Long, CourseChapterModel> {

    boolean existsByCourseJidAndAlias(String courseJid, String alias);

    boolean existsByCourseJidAndChapterJid(String courseJid, String chapterJid);
}
