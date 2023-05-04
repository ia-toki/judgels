package judgels.sandalphon.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ProblemDao extends JudgelsDao<ProblemModel> {
    ProblemQueryBuilder select();
    Optional<ProblemModel> selectBySlug(String slug);

    interface ProblemQueryBuilder extends QueryBuilder<ProblemModel> {
        ProblemQueryBuilder whereUserCanView(String userJid);
        ProblemQueryBuilder whereTermsMatch(String term);
        ProblemQueryBuilder whereTagsMatch(List<Set<String>> tagGroups);
    }
}
