package judgels.jerahmeel.persistence;

import java.util.List;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface ChapterLessonDao extends Dao<ChapterLessonModel> {
    List<ChapterLessonModel> selectAllByChapterJid(String chapterJid, SelectionOptions options);
}
