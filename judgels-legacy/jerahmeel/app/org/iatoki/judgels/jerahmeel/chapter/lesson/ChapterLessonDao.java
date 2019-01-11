package org.iatoki.judgels.jerahmeel.chapter.lesson;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

@ImplementedBy(ChapterLessonHibernateDao.class)
public interface ChapterLessonDao extends Dao<Long, ChapterLessonModel> {

    boolean existsByChapterJidAndAlias(String chapterJid, String alias);
}
