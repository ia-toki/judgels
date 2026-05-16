package judgels.persistence;

import java.util.Collection;
import java.util.Optional;

public interface LessonDao extends JudgelsDao<LessonModel> {
    LessonQueryBuilder select();
    Optional<LessonModel> selectBySlug(String slug);

    interface LessonQueryBuilder extends QueryBuilder<LessonModel> {
        LessonQueryBuilder whereUserCanView(String userJid);
        LessonQueryBuilder whereTermsMatch(String term);
        LessonQueryBuilder whereSlugIn(Collection<String> slugs);
    }
}
