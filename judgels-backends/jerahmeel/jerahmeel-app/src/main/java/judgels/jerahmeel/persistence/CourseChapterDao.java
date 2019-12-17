package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface CourseChapterDao extends Dao<CourseChapterModel> {
    Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias);
    List<CourseChapterModel> selectAllByCourseJid(String contestJid, SelectionOptions options);
    List<CourseChapterModel> selectAllByChapterJids(Set<String> chapterJid);
}
