package org.iatoki.judgels.jerahmeel.chapter.dependency;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ChapterDependencyHibernateDao.class)
public interface ChapterDependencyDao extends Dao<Long, ChapterDependencyModel> {

    boolean existsByChapterJidAndDependencyJid(String chapterJid, String dependencyJid);

    List<ChapterDependencyModel> getByChapterJid(String chapterJid);
}
