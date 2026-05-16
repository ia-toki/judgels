package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    ProblemSetQueryBuilder select();

    Optional<ProblemSetModel> selectBySlug(String problemSetSlug);
    List<ProblemSetModel> selectAllBySlugs(Collection<String> contestSlugs);

    interface ProblemSetQueryBuilder extends QueryBuilder<ProblemSetModel> {
        ProblemSetQueryBuilder whereArchiveIs(String archiveJid);
        ProblemSetQueryBuilder whereNameLike(String name);
    }
}
