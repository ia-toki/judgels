package judgels.persistence.dao;

import java.util.Optional;
import judgels.persistence.QueryBuilder;
import judgels.persistence.model.ChapterLessonModel;

public interface ChapterLessonDao extends Dao<ChapterLessonModel> {
    QueryBuilder<ChapterLessonModel> selectByChapterJid(String contestJid);

    Optional<ChapterLessonModel> selectByLessonJid(String lessonJid);
    Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias);
}
