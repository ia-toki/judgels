package judgels.sandalphon.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface LessonDao extends JudgelsDao<LessonModel> {
    LessonQueryBuilder select();
    Optional<LessonModel> selectUniqueBySlug(String slug);

    interface LessonQueryBuilder extends QueryBuilder<LessonModel> {
        LessonQueryBuilder whereUserCanView(String userJid, boolean isAdmin);
        LessonQueryBuilder whereTermsMatch(String term);
    }
}
