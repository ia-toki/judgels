package judgels.jerahmeel.persistence;

import java.util.Optional;
import judgels.persistence.JudgelsDao;
import judgels.persistence.QueryBuilder;

public interface ProblemSetDao extends JudgelsDao<ProblemSetModel> {
    ProblemSetQueryBuilder select();

    Optional<ProblemSetModel> selectBySlug(String problemSetSlug);

    interface ProblemSetQueryBuilder extends QueryBuilder<ProblemSetModel> {
        ProblemSetQueryBuilder whereArchiveIs(String archiveJid);
        ProblemSetQueryBuilder whereNameLike(String name);
    }
}
