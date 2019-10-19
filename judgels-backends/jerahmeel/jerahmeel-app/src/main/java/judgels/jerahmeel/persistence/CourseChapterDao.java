package judgels.jerahmeel.persistence;

import java.util.List;
import judgels.persistence.Dao;
import judgels.persistence.api.SelectionOptions;

public interface CourseChapterDao extends Dao<CourseChapterModel> {
    List<CourseChapterModel> selectAllByCourseJid(String contestJid, SelectionOptions options);
}
