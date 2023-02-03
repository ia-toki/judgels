package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ChapterLessonDao extends Dao<ChapterLessonModel> {
    Optional<ChapterLessonModel> selectByLessonJid(String lessonJid);
    Optional<ChapterLessonModel> selectByChapterJidAndLessonAlias(String chapterJid, String lessonAlias);
    List<ChapterLessonModel> selectAllByChapterJid(String chapterJid, SelectionOptions options);
}
