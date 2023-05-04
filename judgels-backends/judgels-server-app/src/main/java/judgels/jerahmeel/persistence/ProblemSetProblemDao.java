package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.QueryBuilder;
import judgels.persistence.api.Page;

public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {
    QueryBuilder<ProblemSetProblemModel> selectByProblemSetJid(String problemSetJid);
    QueryBuilder<ProblemSetProblemModel> selectByProblemSetJids(Set<String> problemSetJids);

    List<ProblemSetProblemModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetProblemModel> selectAllByProblemJids(Set<String> problemJids);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemJid(String problemSetJid, String problemJid);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(String problemSetJid, String problemAlias);
    Page<ProblemSetProblemModel> selectPagedByDifficulty(Set<String> allowedProblemJids, int pageNumber, int pageSize);
}
