package judgels.sandalphon.persistence;

import java.util.List;
import judgels.persistence.JudgelsDao;

public interface LessonDao extends JudgelsDao<LessonModel> {

    List<String> getJidsByAuthorJid(String authorJid);

    LessonModel findBySlug(String slug);

    boolean existsBySlug(String slug);
}
