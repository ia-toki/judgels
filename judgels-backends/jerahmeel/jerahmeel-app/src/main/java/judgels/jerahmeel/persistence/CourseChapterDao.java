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
    long selectCountByCourseJid(String courseJid);
    List<CourseChapterModel> selectAllByChapterJids(Set<String> chapterJid);
}
