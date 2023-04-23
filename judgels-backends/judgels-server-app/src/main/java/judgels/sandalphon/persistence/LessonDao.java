package judgels.sandalphon.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface LessonDao extends JudgelsDao<LessonModel> {
    Optional<LessonModel> selectBySlug(String slug);
    Page<LessonModel> selectPaged(String termFilter, SelectionOptions options);
    Page<LessonModel> selectPagedByUserJid(String userJid, String termFilter, SelectionOptions options);
}
