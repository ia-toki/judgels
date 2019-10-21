package org.iatoki.judgels.jerahmeel.chapter.lesson;

import com.google.inject.ImplementedBy;
import judgels.jerahmeel.persistence.ChapterLessonModel;
import judgels.persistence.Dao;

@ImplementedBy(ChapterLessonHibernateDao.class)
public interface ChapterLessonDao extends Dao<ChapterLessonModel> {

    boolean existsByChapterJidAndAlias(String chapterJid, String alias);
}
