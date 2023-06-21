package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    ProblemSetQueryBuilder select();

    Optional<ProblemSetModel> selectBySlug(String problemSetSlug);
    List<ProblemSetModel> selectAllBySlugs(Set<String> contestSlugs);

    interface ProblemSetQueryBuilder extends QueryBuilder<ProblemSetModel> {
        ProblemSetQueryBuilder whereArchiveIs(String archiveJid);
        ProblemSetQueryBuilder whereNameLike(String name);
    }
}
