package judgels.jerahmeel.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import judgels.persistence.Dao;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;

public interface ProblemSetProblemDao extends Dao<ProblemSetProblemModel> {
    List<ProblemSetProblemModel> selectAllByProblemJid(String problemJid);
    List<ProblemSetProblemModel> selectAllByProblemJids(Set<String> problemJids);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemJid(String problemSetJid, String problemJid);
    Optional<ProblemSetProblemModel> selectByProblemSetJidAndProblemAlias(String problemSetJid, String problemAlias);
    List<ProblemSetProblemModel> selectAllByProblemSetJid(String problemSetJid, SelectionOptions options);
    List<ProblemSetProblemModel> selectAllByProblemSetJids(Set<String> problemSetJids, SelectionOptions options);
    Page<ProblemSetProblemModel> selectPagedByDifficulty(Set<String> allowedProblemJids, SelectionOptions options);
}
