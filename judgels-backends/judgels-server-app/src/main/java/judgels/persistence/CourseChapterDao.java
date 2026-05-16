package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CourseChapterDao extends Dao<CourseChapterModel> {
    Optional<CourseChapterModel> selectByCourseJidAndChapterAlias(String courseJid, String chapterAlias);
    Optional<CourseChapterModel> selectByChapterJid(String chapterJid);
    QueryBuilder<CourseChapterModel> selectByCourseJid(String courseJid);
    List<CourseChapterModel> selectAllByChapterJids(Collection<String> chapterJid);
}
