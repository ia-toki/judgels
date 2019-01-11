package org.iatoki.judgels.jerahmeel.chapter.problem;

import com.google.inject.ImplementedBy;
import org.iatoki.judgels.play.model.Dao;

import java.util.List;

@ImplementedBy(ChapterProblemHibernateDao.class)
public interface ChapterProblemDao extends Dao<Long, ChapterProblemModel> {

    boolean existsByChapterJidAndAlias(String chapterJid, String alias);

    List<ChapterProblemModel> getByChapterJid(String chapterJid);

    ChapterProblemModel findByChapterJidAndProblemJid(String chapterJid, String problemJid);
}
