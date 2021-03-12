package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {
    Optional<ProblemSetProblemModel> selectByProblemJid(String problemJid);
    List<ProblemSetProblemModel> selectAllByProblemJids(Set<String> problemJids);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(String problemSetJid, String problemAlias);
    List<ProblemSetProblemModel> selectAllByProblemSetJid(String problemSetJid, SelectionOptions options);
    Map<String, Long> selectCountsByProblemSetJids(Set<String> problemSetJids);
    Page<ProblemSetProblemModel> selectPagedByDifficulty(Set<String> allowedProblemJids, SelectionOptions options);
}
