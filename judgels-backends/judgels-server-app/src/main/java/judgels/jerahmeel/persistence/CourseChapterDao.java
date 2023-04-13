package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface CourseChapterDao extends Dao<CourseChapterModel> {
    Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias);
    Optional<CourseChapterModel> selectByChapterJid(String chapterJid);
    List<CourseChapterModel> selectAllByCourseJid(String courseJid, SelectionOptions options);
    List<CourseChapterModel> selectAllByCourseJids(Set<String> courseJids);
    List<CourseChapterModel> selectAllByChapterJids(Set<String> chapterJid);
}
