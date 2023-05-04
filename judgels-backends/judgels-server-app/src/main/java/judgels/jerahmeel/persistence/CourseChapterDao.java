package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;

public interface CourseChapterDao extends Dao<CourseChapterModel> {
    Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias);
    Optional<CourseChapterModel> selectByChapterJid(String chapterJid);
    QueryBuilder<CourseChapterModel> selectByCourseJid(String courseJid);
    List<CourseChapterModel> selectAllByChapterJids(Set<String> chapterJid);
}
