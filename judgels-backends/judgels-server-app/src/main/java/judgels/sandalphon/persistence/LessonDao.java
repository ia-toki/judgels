package judgels.sandalphon.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface LessonDao extends JudgelsDao<LessonModel> {
    Page<LessonModel> selectPaged(String termFilter, SelectionOptions options);
    Page<LessonModel> selectPagedByUserJid(String userJid, String termFilter, SelectionOptions options);

    List<String> getJidsByAuthorJid(String authorJid);

    LessonModel findBySlug(String slug);

    boolean existsBySlug(String slug);
}
